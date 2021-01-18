package com.its.springjwt.controllers;

import com.its.springjwt.models.ERole;
import com.its.springjwt.models.Role;
import com.its.springjwt.models.User;
import com.its.springjwt.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class, setupBefore = TestExecutionEvent.TEST_EXECUTION)
@interface WithMockCustomUser {

    String username() default "test";
    String password() default "12341234";

}

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Autowired
    PasswordEncoder encoder;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Set<Role> roles = new HashSet<>();
        Role role = new Role(ERole.ROLE_USER);
        role.setId(0);
        roles.add(role);

        User basicUser = new User();
        basicUser.setId(0L);
        basicUser.setUsername("test");
        basicUser.setPassword(encoder.encode("12341234"));
        basicUser.setEmail("test");
        basicUser.setProfilePic("testjpg");
        basicUser.setRegistered_at("27-02-2020");
        basicUser.setRoles(roles);

        UserDetails u = UserDetailsImpl.build(basicUser);


        Authentication auth =
                new UsernamePasswordAuthenticationToken(u, "12341234", u.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}