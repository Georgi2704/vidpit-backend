package com.its.springjwt.controllers;

import com.its.springjwt.models.ERole;
import com.its.springjwt.models.Role;
import com.its.springjwt.models.User;
import com.its.springjwt.security.services.UserDetailsImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.*;
import java.util.stream.Collectors;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

    @Bean(name = "MyFoo")
    @Primary
    public UserDetailsService userDetailsService(){
        Set<Role> roles = new HashSet<>();
        Role role = new Role(ERole.ROLE_USER);
        role.setId(0);
        roles.add(role);

        User basicUser = new User();
        basicUser.setUsername("test");
        basicUser.setPassword("12341234");
        basicUser.setEmail("test");
        basicUser.setProfilePic("testjpg");
        basicUser.setRegistered_at("27-02-2020");
        basicUser.setRoles(roles);

        UserDetails u = UserDetailsImpl.build(basicUser);

        return new InMemoryUserDetailsManager(Arrays.asList(u));
    }

}
