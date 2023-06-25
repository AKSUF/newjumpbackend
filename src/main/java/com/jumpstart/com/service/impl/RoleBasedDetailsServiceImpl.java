package com.jumpstart.com.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.RoleBasedDetails;
import com.jumpstart.com.entities.SignUpRole;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.RoleBasedDetailsDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.RoleBasedDetailsRepository;
import com.jumpstart.com.repository.SignUpRoleRepository;
import com.jumpstart.com.service.FileService;
import com.jumpstart.com.service.RoleBasedDetailsService;


@Service
public class RoleBasedDetailsServiceImpl implements RoleBasedDetailsService {

	@Autowired
	private SignUpRoleRepository signUpRoleRepository;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private RoleBasedDetailsRepository roleBasedDetailsRepository;
	
	@Value("${project.image}")
	private String path;
	
	
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private AccountRepository accountRepository;
	
	


	@Override
	public void uploadImage(MultipartFile multipartFile,Long srid) throws IOException {
		RoleBasedDetails roleBasedDetails = roleBasedDetailsRepository.findById(srid)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", srid.toString()));

		// deleting old image
		if (roleBasedDetails.getProfile_image() != null) {
			this.fileService.deleteFile(roleBasedDetails.getProfile_image());
		}

		// getting new file name
		String uploadedImage = fileService.uploadImage(path,multipartFile);

		// setting image name
		roleBasedDetails.setProfile_image(uploadedImage);

		// updating user
		this.roleBasedDetailsRepository.save(roleBasedDetails);
	}
	
	@Override
	public RoleBasedDetailsDto createUserProfile(RoleBasedDetailsDto roleBasedDetailsDto, Long srid) {

		SignUpRole signUpRole = signUpRoleRepository.findById(srid)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", srid.toString()));

//		RoleBasedDetails roleBasedDetails = signUpRole.getRoleBasedDetails();
		RoleBasedDetails roleBasedDetails = this.modelMapper.map(roleBasedDetailsDto, RoleBasedDetails.class);
		roleBasedDetails.setSignUpRole(signUpRole);
		roleBasedDetails.setProfile_image("default.png");
		roleBasedDetails = roleBasedDetailsRepository.save(roleBasedDetails);
		return this.modelMapper.map(roleBasedDetails, RoleBasedDetailsDto.class);
	}
	
	
	//After selecting the email, password, role, the user has to provide some information
	@Override
	public RoleBasedDetailsDto createShopkeerperProfile(RoleBasedDetailsDto roleBasedDetailsDto, Long srid, Long sid) {

		SignUpRole signUpRole = signUpRoleRepository.findById(srid)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", srid.toString()));

		RoleBasedDetails roleBasedDetails = this.modelMapper.map(roleBasedDetailsDto, RoleBasedDetails.class);
		roleBasedDetails.setSignUpRole(signUpRole);
		roleBasedDetails.setStoreId(sid);
		roleBasedDetails.setProfile_image("default.png");
		roleBasedDetails = roleBasedDetailsRepository.save(roleBasedDetails);
		return this.modelMapper.map(roleBasedDetails, RoleBasedDetailsDto.class);
	}
	
	// using token we will get specific user details
	@Override
	public RoleBasedDetailsDto getUserRoleDetails(String token) {
		RoleBasedDetails product = this.roleBasedDetailsRepository.findByToken(token)
				.orElseThrow(() -> new ResourceNotFoundException("product", "product id",token));

		return this.modelMapper.map(product, RoleBasedDetailsDto.class);
	}
	
	
	
	// if anyone want to register as employee, shopkeeper, rider and producer he/she need to register 
	@Override
	public RoleBasedDetailsDto createToleProfile(RoleBasedDetailsDto roleBasedDetailsDto, String token) {
		SignUpRole signUpRole = signUpRoleRepository.findByToken(token);
		RoleBasedDetails roleBasedDetails = this.modelMapper.map(roleBasedDetailsDto, RoleBasedDetails.class);
		if(token == roleBasedDetails.getToken()) {
			   throw new RuntimeException("Token is invalid. Registration cannot be confirmed.");
			}
		roleBasedDetails.setSignUpRole(signUpRole);
		roleBasedDetails.setProfile_image("default.png");
		roleBasedDetails.setToken(token);
		roleBasedDetails = roleBasedDetailsRepository.save(roleBasedDetails);
	
		// Send confirmation email to admin
	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setFrom(roleBasedDetails.getSignUpRole().getEmail());
	    message.setTo("abu053125@gmail.com");
	    message.setSubject("New user registration");
	    message.setText("A new user has registered with the following details:\n\n" + "Email: " + roleBasedDetails.getSignUpRole().getEmail()
	            + "\n\n" + "Please accept or reject the registration request by visiting:\n\n"
	            + "http://localhost:3000/admin/user-request/" + roleBasedDetails.getToken());
	    mailSender.send(message);
		return this.modelMapper.map(roleBasedDetails, RoleBasedDetailsDto.class);
	}
	@Override
	public List<RoleBasedDetailsDto> getAllUserRequest() {
	    List<RoleBasedDetails> allUser = this.roleBasedDetailsRepository.findAll();
	    List<Account> allAccounts = this.accountRepository.findAll();
	    Set<String> accountEmails = allAccounts.stream().map(Account::getEmail).collect(Collectors.toSet());

	    List<RoleBasedDetailsDto> nonMatchingUserDto = allUser.stream()
	            .filter(rbDetails -> !accountEmails.contains(rbDetails.getSignUpRole().getEmail())) // Filter based on email non-match
	            .map(rbDetails -> this.modelMapper.map(rbDetails, RoleBasedDetailsDto.class))
	            .collect(Collectors.toList());

	    return nonMatchingUserDto;
	}
	
	
	@Override
	public void deleteRoleBasedDetailsDto(String token) {
		RoleBasedDetails roleBasedDetails = this.roleBasedDetailsRepository.findByToken(token).orElseThrow(()-> 
		new ResourceNotFoundException("Role Based Details", "Role Based Details id", token));
		this.roleBasedDetailsRepository.delete(roleBasedDetails);
		
	}
	
	
}
