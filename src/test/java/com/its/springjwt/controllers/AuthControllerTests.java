package com.its.springjwt.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import com.its.springjwt.models.ERole;
import com.its.springjwt.models.Role;
import com.its.springjwt.models.User;
import com.its.springjwt.models.UserEdited;
import com.its.springjwt.payload.response.MessageResponse;
import com.its.springjwt.payload.request.LoginRequest;
import com.its.springjwt.payload.request.SignupRequest;
import com.its.springjwt.payload.response.JwtResponse;
import com.its.springjwt.payload.response.MessageResponse;
import com.its.springjwt.repository.RoleRepository;
import com.its.springjwt.repository.UserRepository;
import com.its.springjwt.security.jwt.JwtUtils;
import com.its.springjwt.security.services.UserDetailsImpl;
import com.its.springjwt.security.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Valid;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

@WebMvcTest(AuthController.class)
public class AuthControllerTests {

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
    @MockBean
    PasswordEncoder encoder;
//    @MockBean
//    SpringSecurityWebAuxTestConfig testConfig;
    @MockBean
    WithMockCustomUserSecurityContextFactory mck;
    @MockBean
    AuthenticationManager authenticationManager;

    @Test
    public void testRegisterUser_NoRole() throws Exception{
        List <Role> listRoles = new ArrayList<>();
        listRoles.add(new Role(ERole.ROLE_USER));
        listRoles.get(0).setId(0);
        listRoles.add(new Role(ERole.ROLE_MODERATOR));
        listRoles.get(1).setId(1);
        listRoles.add(new Role(ERole.ROLE_ADMIN));
        listRoles.get(2).setId(2);
        Mockito.when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(java.util.Optional.ofNullable(listRoles.get(0)));
        Mockito.when(roleRepository.findByName(ERole.ROLE_MODERATOR)).thenReturn(java.util.Optional.ofNullable(listRoles.get(1)));
        Mockito.when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(java.util.Optional.ofNullable(listRoles.get(2)));
        Set<String> currentRoles = null;

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("test");
        signupRequest.setEmail("test");
        signupRequest.setPassword("12341234");
        signupRequest.setRole(currentRoles);

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword());
        Set<String> strRoles = new HashSet<String>();
        strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        Mockito.when(userRepository.save(user)).thenReturn(user);

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }

        user.setRoles(roles);
        String url = "/auth/signup";

        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest))).andExpect(status().isOk()).andReturn();
        int status = mvcResult.getResponse().getStatus();
        System.out.println(status);
    }

    @Test
    public void testRegisterUser_Admin() throws Exception{
        List <Role> listRoles = new ArrayList<>();
        listRoles.add(new Role(ERole.ROLE_USER));
        listRoles.get(0).setId(0);
        listRoles.add(new Role(ERole.ROLE_MODERATOR));
        listRoles.get(1).setId(1);
        listRoles.add(new Role(ERole.ROLE_ADMIN));
        listRoles.get(2).setId(2);
        Mockito.when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(java.util.Optional.ofNullable(listRoles.get(0)));
        Mockito.when(roleRepository.findByName(ERole.ROLE_MODERATOR)).thenReturn(java.util.Optional.ofNullable(listRoles.get(1)));
        Mockito.when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(java.util.Optional.ofNullable(listRoles.get(2)));
        Set<String> currentRoles = new HashSet<>();
        currentRoles.add("admin");

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("test");
        signupRequest.setEmail("test");
        signupRequest.setPassword("12341234");
        signupRequest.setRole(currentRoles);

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
             signupRequest.getPassword());

        Set<Role> roles = new HashSet<>();
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(adminRole);

        user.setRoles(roles);
        String url = "/auth/signup";

        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest))).andExpect(status().isOk()).andReturn();
        int status = mvcResult.getResponse().getStatus();
        System.out.println(status);
    }

    @Test
    public void testRegisterUser_User() throws Exception{
        List <Role> listRoles = new ArrayList<>();
        listRoles.add(new Role(ERole.ROLE_USER));
        listRoles.get(0).setId(0);
        listRoles.add(new Role(ERole.ROLE_MODERATOR));
        listRoles.get(1).setId(1);
        listRoles.add(new Role(ERole.ROLE_ADMIN));
        listRoles.get(2).setId(2);
        Mockito.when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(java.util.Optional.ofNullable(listRoles.get(0)));
        Mockito.when(roleRepository.findByName(ERole.ROLE_MODERATOR)).thenReturn(java.util.Optional.ofNullable(listRoles.get(1)));
        Mockito.when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(java.util.Optional.ofNullable(listRoles.get(2)));
        Set<String> currentRoles = new HashSet<>();
        currentRoles.add("user");

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("test");
        signupRequest.setEmail("test");
        signupRequest.setPassword("12341234");
        signupRequest.setRole(currentRoles);

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword());

        Set<Role> roles = new HashSet<>();
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        String url = "/auth/signup";

        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest))).andExpect(status().isOk()).andReturn();
        int status = mvcResult.getResponse().getStatus();
        System.out.println(status);
    }


    @Test
    public void testRegisterUser_Mod() throws Exception{
        List <Role> listRoles = new ArrayList<>();
        listRoles.add(new Role(ERole.ROLE_USER));
        listRoles.get(0).setId(0);
        listRoles.add(new Role(ERole.ROLE_MODERATOR));
        listRoles.get(1).setId(1);
        listRoles.add(new Role(ERole.ROLE_ADMIN));
        listRoles.get(2).setId(2);
        Mockito.when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(java.util.Optional.ofNullable(listRoles.get(0)));
        Mockito.when(roleRepository.findByName(ERole.ROLE_MODERATOR)).thenReturn(java.util.Optional.ofNullable(listRoles.get(1)));
        Mockito.when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(java.util.Optional.ofNullable(listRoles.get(2)));
        Set<String> currentRoles = new HashSet<>();
        currentRoles.add("mod");

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("test");
        signupRequest.setEmail("test");
        signupRequest.setPassword("12341234");
        signupRequest.setRole(currentRoles);

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword());

        Set<Role> roles = new HashSet<>();
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(modRole);

        user.setRoles(roles);

        String url = "/auth/signup";

        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest))).andExpect(status().isOk()).andReturn();
        int status = mvcResult.getResponse().getStatus();
        System.out.println(status);
    }

    @Test
    public void testRegisterUser_AlreadyExists() throws Exception{
        //ara
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("test");
        signupRequest.setEmail("test");
        signupRequest.setPassword("12341234");

        Mockito.when(userRepository.existsByUsername(signupRequest.getUsername())).thenReturn(true);
        Mockito.when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);
        String url = "/auth/signup";

        //act
        MvcResult mvcResult = mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(signupRequest)))

                //assert
                .andExpect(status().isBadRequest()).andReturn();
        int status = mvcResult.getResponse().getStatus();
        System.out.println(status);
    }

    @WithMockCustomUser(username = "One")
    @Test
    public void testLoginUser() throws Exception{
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("12341234");
        loginRequest.setUsername("One");

        Mockito.when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()))).thenReturn(SecurityContextHolder.getContext().getAuthentication());

        String url = "/auth/signin";
        MvcResult mvcResult = mockMvc.perform(post(url)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest))).andExpect(status().isOk()).andReturn();
    }


    @Test
    public void testSearchUser() throws Exception {
        List<User> listUsers = new ArrayList<>();
        listUsers.add(new User("One", "One", "12341234"));
        listUsers.add(new User("Two", "Two", "12341234"));
        listUsers.add(new User("Three", "Three", "12341234"));
        List<User> found = new ArrayList();
        found.add(listUsers.get(0));
        found.add(listUsers.get(1));

        String username = "o";

        Mockito.when(userRepository.findByUsernameContaining(username)).thenReturn(found);

        String url = "/auth/users";
        mockMvc.perform(get(url).param("username", username)).andExpect(status().isOk());
    }
}


