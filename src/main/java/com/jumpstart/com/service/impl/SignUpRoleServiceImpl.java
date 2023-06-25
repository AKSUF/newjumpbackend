package com.jumpstart.com.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jumpstart.com.entities.Role;
import com.jumpstart.com.entities.SignUpRole;
import com.jumpstart.com.exception.ApiException;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.SignUpRoleDto;
import com.jumpstart.com.repository.RoleRepository;
import com.jumpstart.com.repository.SignUpRoleRepository;
import com.jumpstart.com.service.SignUpRoleService;

@Service
public class SignUpRoleServiceImpl implements SignUpRoleService {
	@Autowired
	private SignUpRoleRepository signUpRoleRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void registerUserUsingRole(SignUpRole user, Long rid) {


	    
	    // Check if email is already in use
	    if (signUpRoleRepository.existsByEmail(user.getEmail())) {
	        throw new ApiException("Email is already in use");
	    }

	  
	    // Save user in database with a random UUID as a token
	    Role role = this.roleRepository.findById(rid)
	            .orElseThrow(() -> new ResourceNotFoundException("Role", "id", rid.toString()));
	    
//	    user.setToken(UUID.randomUUID().toString());
	    user.setRoleId(role.getRole_id()); // set role ID
	    user.setAddedDate(new Date());
	    user.setPassword(passwordEncoder.encode(user.getPassword()));
	    signUpRoleRepository.save(user);
	    
	    // Send confirmation email to admin
//	    SimpleMailMessage message = new SimpleMailMessage();
//	    message.setFrom(user.getEmail());
//	    message.setTo("rehnumayekhushboo812@gmail.com");
//	    message.setSubject("New user registration");
//	    message.setText("A new user has registered with the following details:\n\n" + "Email: " + user.getEmail()
//	            + "\n\n" + "Please accept or reject the registration request by visiting:\n\n"
//	            + "http://localhost:8080/api/users/" + user.getSignUpRoleId() + "/confirm?token=" + user.getToken());
//	    mailSender.send(message);
	}
	
	@Override
	public List<SignUpRoleDto> getAcceptProducts() {
	    List<SignUpRole> acceptedProductReq = this.signUpRoleRepository.findAll();
	    List<SignUpRoleDto> acceptedProductDtoList = new ArrayList<>();
	    for (SignUpRole signUpRole : acceptedProductReq) {
	        SignUpRoleDto signUpRoleDto = new SignUpRoleDto();
	        // populate the fields of the signUpRoleDto object using the signUpRole object
	        signUpRoleDto.setSignUpRoleId(signUpRole.getSignUpRoleId());
	        signUpRoleDto.setEmail(signUpRole.getEmail());
	        signUpRoleDto.setPassword(signUpRole.getPassword());
	        signUpRoleDto.setAddedDate(signUpRole.getAddedDate());
	        signUpRoleDto.setRoleId(signUpRole.getRoleId());
	      
	        // add the signUpRoleDto object to the list
	        acceptedProductDtoList.add(signUpRoleDto);
	    }
	    return acceptedProductDtoList;
	}
}
