package com.jumpstart.com.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jumpstart.com.config.AppConstants;
import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Role;
import com.jumpstart.com.entities.UserRole;
import com.jumpstart.com.exception.BadRequestException;
import com.jumpstart.com.payloads.AuthResponse;
import com.jumpstart.com.payloads.LoginRequest;
import com.jumpstart.com.payloads.SignUpRequest;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.RoleRepository;
import com.jumpstart.com.repository.UserRoleRepository;
import com.jumpstart.com.status.Provider;
import com.jumpstart.com.utils.JwtUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtils jwtUtils;
	
	// User Login
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.createToken(authentication);
		return ResponseEntity.ok(new AuthResponse(token));
	}

	
	//User Sign Up
	@PostMapping("/signUp")
	public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
		if (accountRepo.findByEmail(signUpRequest.getEmail()).isPresent()) {
			throw new BadRequestException("Email address already in use.");
		}
		Account account = new Account();
		account.setEmail(signUpRequest.getEmail());
		account.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		account.setProvider(Provider.local.name());
		account.setAddedDate(new Date());
		account = accountRepo.save(account);
		UserRole userRole = new UserRole();
		Role role = roleRepository.findById(AppConstants.ROLE_USER.longValue()).get();
		userRole.setAccount(account);
		userRole.setRole(role);
		userRoleRepository.save(userRole);
		Authentication authentication = authenticationManager
				.authenticate
				(new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), 
						signUpRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.createToken(authentication);
		return ResponseEntity.ok(new AuthResponse(token));
	}
	
	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
	
	
}
