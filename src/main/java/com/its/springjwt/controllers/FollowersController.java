package com.its.springjwt.controllers;


import com.its.springjwt.models.Category;
import com.its.springjwt.models.Followers;
import com.its.springjwt.models.User;
import com.its.springjwt.models.Video;
import com.its.springjwt.payload.response.MessageResponse;
import com.its.springjwt.payload.response.StatisticsYearResponse;
import com.its.springjwt.repository.CategoryRepository;
import com.its.springjwt.repository.FollowersRepository;
import com.its.springjwt.repository.RoleRepository;
import com.its.springjwt.repository.UserRepository;
import com.its.springjwt.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/follow")
public class FollowersController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    FollowersRepository followersRepo;


    @CrossOrigin
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @PostMapping("/user/{id}")
    public ResponseEntity<MessageResponse> follow(Authentication authentication, @PathVariable Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        long userID = userDetails.getId();
        Optional<User> AuthenticatedUser = userRepo.findById(userID);
        User followedBy;
        User followed;

        if (AuthenticatedUser.isPresent()) {
            followedBy = AuthenticatedUser.get();
        } else {
            throw new UserNotFoundException("id-" + userID);
        }

        Optional<User> following = userRepo.findById(id);
        if (following.isPresent()) {
            followed = following.get();
        } else {
            throw new UserNotFoundException("id-" + id);
        }
        Followers result = new Followers(followedBy, followed, true);

        Optional<Followers> exists = followersRepo.findTopByFollowed_IdAndFollowedBy_IdOrderByIdDesc(followed.getId(), followedBy.getId());
        if (exists.isPresent()) {
            Followers checkF = exists.get();
            LocalDateTime today = LocalDateTime.now();

            if (checkF.getDateOfFollow().toString().equals(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).format(today))) {
                //This is to prevent spamming the follow functionality
                followersRepo.deleteById(checkF.getId());
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Your follow/unfollow won't be counted"));
            }
            if (checkF.isFollow()) {
                result.setFollow(false);
                followersRepo.save(result);
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Unfollowed"));
            }
            followersRepo.save(result);
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("You have followed again"));
        }

        followersRepo.save(result);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("First time following."));
    }

    @CrossOrigin
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/user/{id}")
    public StatisticsYearResponse getStatsFromYear(@PathVariable Long id, @RequestParam("year") int year) {
        List<Integer> dataFollowers = new ArrayList<Integer>();
        List<Integer> dataUnFollowers = new ArrayList<Integer>();
        for (int i = 0; i < 12; i++) {
            Date startDateFor = new GregorianCalendar(year, i, 1).getTime();
            YearMonth yearMonthObject = YearMonth.of(year, i + 1);
            Date endDateFor = new GregorianCalendar(year, i, yearMonthObject.lengthOfMonth()).getTime();

            List<Followers> followersList = followersRepo.findAllByFollowed_IdAndIsFollowAndDateOfFollowBetween(id, true, startDateFor, endDateFor);
            List<Followers> unfollowersList = followersRepo.findAllByFollowed_IdAndIsFollowAndDateOfFollowBetween(id, false, startDateFor, endDateFor);
            dataFollowers.add(followersList.size());
            dataUnFollowers.add(unfollowersList.size());
        }
        return new StatisticsYearResponse(dataFollowers, dataUnFollowers);
    }

    @CrossOrigin
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/user/followers/{id}")
    public long getFollowersCount(@PathVariable Long id) {
            List<Followers> followersList = followersRepo.findAllByFollowed_IdAndIsFollow(id, true);
            List<Followers> unfollowersList = followersRepo.findAllByFollowed_IdAndIsFollow(id, false);

            long total = (long)(followersList.size() - unfollowersList.size());
        return total;
    }

    @CrossOrigin
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/user/following/{id}")
    public List<User> getFollowing(@PathVariable Long id) {
        List<User> filtered = new ArrayList<>();
        List<Followers> followersList = followersRepo.findAllByFollowedBy_Id(id);
        Map<Long, Integer> uniqIDs = new HashMap<>();
        for (Followers follower: followersList) {
            Long currentId = follower.getFollowed().getId();
            if (!(uniqIDs.containsKey(currentId))){
                uniqIDs.put(currentId, 1);
            }
            else{
                uniqIDs.put(currentId, uniqIDs.get(currentId) + 1);
            }
        }
        for (Map.Entry<Long, Integer> entry : uniqIDs.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
            if (entry.getValue() % 2 == 0){
                followersList.removeIf(follower -> follower.getFollowed().getId().equals(entry.getKey()));
            }
        }
        for (Followers follower: followersList) {
            if (!(filtered.contains(follower.getFollowed()))){
                User f = follower.getFollowed();
                f.setPassword("");
                filtered.add(f);
            }
        }

        return filtered;
    }

    @CrossOrigin
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @GetMapping("/user/status/{id}")
    public ResponseEntity<MessageResponse> getFollowStatus(Authentication authentication, @PathVariable Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        long userID = userDetails.getId();
        Optional<User> AuthenticatedUser = userRepo.findById(userID);
        User followedBy;
        User followed;

        if (AuthenticatedUser.isPresent()) {
            followedBy = AuthenticatedUser.get();
        } else {
            throw new UserNotFoundException("id-" + userID);
        }

        Optional<User> following = userRepo.findById(id);
        if (following.isPresent()) {
            followed = following.get();
        } else {
            throw new UserNotFoundException("id-" + id);
        }

        Optional<Followers> exists = followersRepo.findTopByFollowed_IdAndFollowedBy_IdOrderByIdDesc(followed.getId(), followedBy.getId());
        if (exists.isPresent()) {
            Followers checkF = exists.get();

            if (checkF.isFollow()) {
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Following"));
            }
            else if(!checkF.isFollow()) {
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Follow"));
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Follow"));
    }
}


