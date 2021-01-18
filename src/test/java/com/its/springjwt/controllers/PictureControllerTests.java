package com.its.springjwt.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.its.springjwt.models.FileInfo;
import com.its.springjwt.models.User;
import com.its.springjwt.repository.UserRepository;
import com.its.springjwt.security.jwt.JwtUtils;
import com.its.springjwt.security.services.UserDetailsServiceImpl;
import com.its.springjwt.service.FilesStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@WebMvcTest(PictureController.class)
@ContextConfiguration
public class PictureControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    FilesStorageService storageService;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder encoder;

    @Test
    public void testUploadPicture() throws Exception {

        long id = 1L;
        String rawPassword = "12341234";
        User testUser = new User( id,"One", "One", encoder.encode(rawPassword), "20-11-2020");
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(testUser));

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        String url = "/file/upload/profilepic/user/" + id;
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        MvcResult mvcResult = mockMvc.perform(multipart(url).file(file)).andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testUploadPictureUserNotFound() throws Exception {

        long id = 1L;
        String rawPassword = "12341234";
        User testUser = new User( id,"One", "One", encoder.encode(rawPassword), "20-11-2020");
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        String url = "/file/upload/profilepic/user/" + id;
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        MvcResult mvcResult = mockMvc.perform(multipart(url).file(file)).andExpect(status().isNotFound()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testUploadPictureCouldNotUpload() throws Exception {

        long id = 1L;
        Mockito.when(userRepository.findById(id)).thenReturn(null);

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        String url = "/file/upload/profilepic/user/" + id;
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        MvcResult mvcResult = mockMvc.perform(multipart(url).file(file)).andExpect(status().isExpectationFailed()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
    }

    @Test
    public void testGetListFiles() throws Exception {

        long id = 1L;

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );


        FileInfo path = Mockito.mock(FileInfo.class);
        List<FileInfo> list = new ArrayList<FileInfo>();
        list.add(path);

        String url = "/file/files";

        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
    }
}
