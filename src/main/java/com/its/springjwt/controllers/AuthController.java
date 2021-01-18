package com.its.springjwt.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.its.springjwt.models.AlreadyExistsError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.its.springjwt.models.ERole;
import com.its.springjwt.models.Role;
import com.its.springjwt.models.User;
import com.its.springjwt.payload.request.LoginRequest;
import com.its.springjwt.payload.request.SignupRequest;
import com.its.springjwt.payload.response.JwtResponse;
import com.its.springjwt.payload.response.MessageResponse;
import com.its.springjwt.repository.RoleRepository;
import com.its.springjwt.repository.UserRepository;
import com.its.springjwt.security.jwt.JwtUtils;
import com.its.springjwt.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")

public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@RequestMapping(value = "/getusername", method = RequestMethod.GET)
	@ResponseBody
	public String currentUserName(Authentication authentication) {
		return authentication.getName();
	}

	@RequestMapping(value = "/getuserdetails", method = RequestMethod.GET)
	@ResponseBody
	public Long currentUserDetails(Authentication authentication) {
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		return userDetails.getId();
	}


	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles, userDetails.getProfilepic()));
	}



	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
		AlreadyExistsError AlreadyExistsError = new AlreadyExistsError();

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			AlreadyExistsError.setUserAlreadyExists(true);
		}
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			AlreadyExistsError.setEmailAlreadyExists(true);
		}

		if (AlreadyExistsError.hasAnyErrors()){
			return ResponseEntity
					.badRequest()
					.body(AlreadyExistsError);
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = new HashSet<String>();
		strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();


		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		}
		else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);
					System.out.println("enters adimn");

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);
					System.out.println("enters mod");

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
					System.out.println("enters user");

				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@CrossOrigin
	@GetMapping("/users")
	@ResponseBody
	public List<User> searchUser(@RequestParam String username){
		return  userRepository.findByUsernameContaining(username);
	}
}
