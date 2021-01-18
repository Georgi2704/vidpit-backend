package com.its.springjwt.controllers;

import com.its.springjwt.models.*;
import com.its.springjwt.repository.RoleRepository;
import com.its.springjwt.repository.UserRepository;
import com.its.springjwt.security.jwt.JwtUtils;
import com.its.springjwt.security.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@WebMvcTest(UserController.class)
@ContextConfiguration
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder encoder;


    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void testListAllUsers_GetAllUsers() throws Exception {
        List<User> listUsers = new ArrayList<>();
        listUsers.add(new User("One", "One", "12341234"));
        listUsers.add(new User("Two", "Two", "12341234"));
        listUsers.add(new User("Three", "Three", "12341234"));

        Mockito.when(userRepository.findAll()).thenReturn(listUsers);
        String url = "/users";
        MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
        int status = mvcResult.getResponse().getStatus();
        System.out.println(status);
        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(actualJsonResponse);
        String expectedJsonResponse = objectMapper.writeValueAsString(listUsers);
        assertEquals(expectedJsonResponse, actualJsonResponse);
    }

    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    @Test
    public void testUpdateUser_ValidBodyProvided_ValidIdProvided() throws Exception{
        long id = 1L;
        User existUser = new User( 1L,"One", "One", "12341234", "20-11-2020");
        User savedUser = new User(1L, "Two", "One", "12341234", "20-11-2020");
        String url = "/user/" + id;
        Mockito.when(userRepository.findById(id)).thenReturn(java.util.Optional.of(existUser));
        Mockito.when(userRepository.save(existUser)).thenReturn(savedUser);
        MvcResult mvcResult = mockMvc.perform(put(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(existUser))).andExpect(status().isOk()).andReturn();
    }

    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    @Test
    public void testUpdateUser_NotFound() throws Exception{
        long id = 1L;
        User existUser = new User( 1L,"One", "One", "12341234", "20-11-2020");
        User savedUser = new User(1L, "Two", "One", "12341234", "20-11-2020");
        String url = "/user/" + id;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.ofNullable(null));
        Mockito.when(userRepository.save(existUser)).thenReturn(savedUser);
        MvcResult mvcResult = mockMvc.perform(put(url)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(existUser))).andExpect(status().isNotFound()).andReturn();
    }

    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    @Test
    public void testDeleteUser_DeleteFromDb_ValidIdProvided() throws Exception{
        Long UserId = 1L;
        String url = "/user/" + UserId;
        User existUser = new User("One", "One", "One");

        Mockito.when(userRepository.findById(UserId)).thenReturn(java.util.Optional.of(existUser));
        Mockito.doNothing().when(userRepository).deleteById(UserId);
        mockMvc.perform(delete(url)).andExpect(status().isNoContent());
        Mockito.verify(userRepository, times(1)).deleteById(UserId);
    }

    @WithMockUser(roles = {"ADMIN", "MODERATOR"})
    @Test
    public void testDeleteUser_notFound() throws Exception{
        Long UserId = 1L;
        String url = "/user/" + UserId;

        Mockito.when(userRepository.findById(UserId)).thenReturn(Optional.ofNullable(null));
        Mockito.doNothing().when(userRepository).deleteById(UserId);
        mockMvc.perform(delete(url)).andExpect(status().isNotFound());
    }

    @WithMockUser(roles = {"ADMIN", "MODERATOR", "USER"})
    @Test
    public void findUserTest() throws Exception {
        List<User> listUsers = new ArrayList<>();
        listUsers.add(new User("One", "One", "12341234"));
        listUsers.add(new User("Two", "Two", "12341234"));
        listUsers.add(new User("Three", "Three", "12341234"));

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(listUsers.get(1)));

        String url = "/user/1";
        mockMvc.perform(get(url)).andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ADMIN", "MODERATOR", "USER"})
    @Test
    public void findUserTest_NotFound() throws Exception {
        List<User> listUsers = new ArrayList<>();
        listUsers.add(new User("One", "One", "12341234"));
        listUsers.add(new User("Two", "Two", "12341234"));
        listUsers.add(new User("Three", "Three", "12341234"));

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        String url = "/user/4";
        mockMvc.perform(get(url)).andExpect(status().isNotFound());
    }


    @WithMockCustomUser(username = "One", password = "12341234")
    @Test
    public void testUpdateUsers4Users() throws Exception{
        long id = 0L;
        String rawPassword = "12341234";
        UserEdited userEdited = new UserEdited("Two", "One", "12341234", "12341234");
        User existUser = new User( 55L,"One", "One", encoder.encode(rawPassword), "20-11-2020");
        String url = "/user/test/" + id;
        Mockito.when(userRepository.findById(id)).thenReturn(java.util.Optional.of(existUser));
        MvcResult mvcResult = mockMvc.perform(put(url)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userEdited))).andExpect(status().isOk()).andReturn();
    }

    @WithMockCustomUser(username = "One", password = "12341234")
    @Test
    public void testUpdateUsers4Users_WrongPassword() throws Exception{
        long id = 0L;
        String rawPassword = "12341234";
        UserEdited userEdited = new UserEdited("Two", "One", "12341234", "12341235");
        User existUser = new User( 55L,"One", "One", encoder.encode(rawPassword), "20-11-2020");
        String url = "/user/test/" + id;
        Mockito.when(userRepository.findById(id)).thenReturn(java.util.Optional.of(existUser));
        MvcResult mvcResult = mockMvc.perform(put(url)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userEdited))).andExpect(status().isUnauthorized()).andReturn();
    }

    @WithMockCustomUser(username = "One", password = "12341234")
    @Test
    public void testUpdateUsers4Users_WrongUser() throws Exception{
        long id = 1L;
        String rawPassword = "12341234";
        UserEdited userEdited = new UserEdited("Two", "One", "12341234", "12341234");
        User existUser = new User( 55L,"One", "One", encoder.encode(rawPassword), "20-11-2020");
        String url = "/user/test/" + id;
        Mockito.when(userRepository.findById(id)).thenReturn(java.util.Optional.of(existUser));
        MvcResult mvcResult = mockMvc.perform(put(url)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userEdited))).andExpect(status().isForbidden()).andReturn();
    }

    @WithMockCustomUser(username = "One", password = "12341234")
    @Test
    public void testUpdateUsers4Users_AlreadyExists() throws Exception{
        long id = 0L;
        String rawPassword = "12341234";
        UserEdited userEdited = new UserEdited("Two", "One", "12341234", "12341234");
        User existUser = new User( 55L,"One", "One", encoder.encode(rawPassword), "20-11-2020");
        String url = "/user/test/" + id;
        Mockito.when(userRepository.findById(id)).thenReturn(java.util.Optional.of(existUser));
        Mockito.when(userRepository.existsByUsername(userEdited.getNewUsername())).thenReturn(true);
        Mockito.when(userRepository.existsByEmail(userEdited.getNewEmail())).thenReturn(true);
        MvcResult mvcResult = mockMvc.perform(put(url)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userEdited))).andExpect(status().isBadRequest()).andReturn();
    }

}
