package com.its.springjwt.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.its.springjwt.models.*;
import com.its.springjwt.payload.response.MessageResponse;
import com.its.springjwt.repository.CategoryRepository;
import com.its.springjwt.repository.RoleRepository;
import com.its.springjwt.repository.UserRepository;
import com.its.springjwt.repository.VideoRepository;
import com.its.springjwt.security.services.UserDetailsImpl;
import com.its.springjwt.service.FilesStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/vid")
public class VideoController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    VideoRepository videoRepo;

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    FilesStorageService storageService;


    @CrossOrigin
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadVideo(Authentication authentication, @RequestParam("video") String videoString, @RequestParam("file") MultipartFile file) throws JsonProcessingException {
        Video video  = new ObjectMapper().readValue(videoString, Video.class);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        long userID = userDetails.getId();
        Optional<User> uploader = userRepo.findById(userID);

        Optional<Category> categoryOptional = categoryRepo.findByName(video.getCategory().getName());
        if (!categoryOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Category not found: " + video.getCategory().getName()));
        }

        String message = "";
        try {
            String currentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String newFileName = file.getOriginalFilename().replace(file.getOriginalFilename(), FilenameUtils.getBaseName(file.getOriginalFilename()).concat(currentDate) + "." + FilenameUtils.getExtension(file.getOriginalFilename())).toLowerCase();
            System.out.println(file.getSize());
            final long limit = 100 * 1024 * 1024;
            if (file.getSize() > limit) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Your video file is too large."));
            }
            storageService.save(file, newFileName, "video");
            Video newVideo = new Video
                    (
                            uploader.get(),
                            video.getTitle(),
                            video.getDescription(),
                            newFileName,
                            categoryOptional.get()
                    );
            videoRepo.save(newVideo);
            message = newFileName;
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not upload the video: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }

        //Video fullVideo = new Video("g", "g", "g", "test", )
    }

    @CrossOrigin
    @GetMapping(value = "/videos/{filename:.+}", produces = {"video/mp4"})
    //@ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename, "video");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @CrossOrigin
    @GetMapping(value = "/videos/video/{id}")
    public Video getVideo(@PathVariable Long id){
        Optional<Video> video = videoRepo.findById(id);

        if (video.isPresent()){
            Video videoReal = video.get();
            User u = videoReal.getUploaded_by();
            u.setPassword("");
            videoReal.setUploaded_by(u);
            return videoReal;
        }
        else {
            throw new UserNotFoundException("video not found" + id);
        }
    }

    @CrossOrigin
    @GetMapping(value = "/thumbnail/{filename:.+}", produces = {"image/jpeg"})
    //@ResponseBody
    public ResponseEntity<Resource> getThumbnail(@PathVariable String filename) {
        Resource file = storageService.load(filename, "thumbnail");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @CrossOrigin
    @GetMapping("/all")
    public List<Video> getNewestVideos() {
        System.out.println("get videos called");
        List<Video> fullDetails = videoRepo.findTop12ByOrderByIdDesc();
        List<Video> hiddenDetails = new ArrayList<>();
        for ( Video v : fullDetails ) {
            User currentUser = new User();
            currentUser.setId(v.getUploaded_by().getId());
            currentUser.setUsername(v.getUploaded_by().getUsername());
            currentUser.setProfilePic(v.getUploaded_by().getProfilePic());
            v.setUploaded_by(currentUser);
            hiddenDetails.add(v);
        }
        return hiddenDetails;
    }

    @CrossOrigin
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @PostMapping("/thumbnail")
    public ResponseEntity<MessageResponse>setThumbnail (Authentication authentication, @RequestParam("video") String videoString, @RequestParam("file") MultipartFile file) throws JsonProcessingException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        long userID = userDetails.getId();
        Video vid = videoRepo.findByVideoContent(videoString).get();
        String message = "";
        if (vid.getUploaded_by().getId() != userID){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("You can't set thumbnail on other users videos!"));
        }

        try {
            String currentDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String newFileName = file.getOriginalFilename().replace(file.getOriginalFilename(), FilenameUtils.getBaseName(file.getOriginalFilename()).concat(currentDate) + "." + FilenameUtils.getExtension(file.getOriginalFilename())).toLowerCase();
            System.out.println(file.getSize());
            final long limit = 10 * 1024 * 1024;
            if (file.getSize() > limit) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Your thumbnail is too large."));
            }
            storageService.save(file, newFileName, "thumbnail");
            vid.setThumbnail(newFileName);
            videoRepo.save(vid);
            message = newFileName;
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }

        //return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("cool message"));
    }

    @Transactional
    @DeleteMapping("/videos/delete/{filename:.+}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deleteFile(@PathVariable String filename) throws IOException {
        //Resource file = storageService.load(filename);
        try{
            videoRepo.deleteByVideoContent(filename);
            storageService.delete(filename, "video");
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Video deleted successfully");
    }

}
