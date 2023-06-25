package com.jumpstart.com.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Role;
import com.jumpstart.com.entities.RoleBasedDetails;
import com.jumpstart.com.entities.SignUpRole;
import com.jumpstart.com.entities.Store;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.entities.UserRole;
import com.jumpstart.com.entities.UserStore;
import com.jumpstart.com.exception.ApiException;
import com.jumpstart.com.exception.BadRequestException;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.AuthResponse;
import com.jumpstart.com.payloads.SignUpRoleDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.RoleBasedDetailsRepository;
import com.jumpstart.com.repository.RoleRepository;
import com.jumpstart.com.repository.SignUpRoleRepository;
import com.jumpstart.com.repository.StoreRepository;
import com.jumpstart.com.repository.UserRepository;
import com.jumpstart.com.repository.UserRoleRepository;
import com.jumpstart.com.repository.UserStoreRepository;
import com.jumpstart.com.service.SignUpRoleService;
import com.jumpstart.com.status.Provider;
import com.jumpstart.com.utils.JwtUtils;

@RestController
@RequestMapping("/auth")
public class SignUpRoleController {
	@Autowired
	private SignUpRoleService signUpRoleService;
	@Autowired
	private SignUpRoleRepository signUpRoleRepository;
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private StoreRepository storeRepository;
	@Autowired
	private UserStoreRepository userStoreRepository;
	@Autowired
	private RoleBasedDetailsRepository basedDetailsRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JavaMailSender mailSender;

	// User can login with email, password and role
	@PostMapping("/registration/{rid}")
	public ResponseEntity<Void> registerUser(@Valid @RequestBody SignUpRole user, @PathVariable Long rid) {
		signUpRoleService.registerUserUsingRole(user, rid);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	// admin can accept user request
	@PostMapping("/{rbdId}/confirm")
	public ResponseEntity<?> confirmUser(@PathVariable Long rbdId) {
		if (rbdId == null) {
		    throw new ApiException("ID parameter cannot be null");
		}
		RoleBasedDetails roleBasedDetails = basedDetailsRepository.findById(rbdId)
	            .orElseThrow(() -> new ResourceNotFoundException("RoleBasedDetails", "id", rbdId.toString()));
		if (accountRepo.findByEmail(roleBasedDetails.getSignUpRole().getEmail()).isPresent()) {
			throw new BadRequestException("Email address already in use.");
		}
		
		
		Account account = new Account();
		account.setEmail(roleBasedDetails.getSignUpRole().getEmail());
		account.setPassword(roleBasedDetails.getSignUpRole().getPassword());
		account.setAddedDate(roleBasedDetails.getSignUpRole().getAddedDate());
		account.setProvider(Provider.local.name());
		account = accountRepo.save(account);

		User user = new User();
		user.setName(roleBasedDetails.getName());
		user.setAbout(roleBasedDetails.getAbout());
		user.setPhone_number(roleBasedDetails.getPhone_number());
		user.setBirth(roleBasedDetails.getBirth());
		user.setGender(roleBasedDetails.getGender());
		user.setProfile_image(roleBasedDetails.getProfile_image());
		user.setDistrict(roleBasedDetails.getDistrict());
		user.setAddress(roleBasedDetails.getAddress());
		user.setTypeOfVehicles(roleBasedDetails.getTypeOfVehicles());
		user.setJumpStartId(roleBasedDetails.getJumpStartId());
		user.setAccount(account);
		user = userRepository.save(user);

		Role role = roleRepository.findById(roleBasedDetails.getSignUpRole().getRoleId())
				.orElseThrow(() -> new ResourceNotFoundException("Role", "id",
						roleBasedDetails.getSignUpRole().getRoleId().toString()));
		UserRole userRole = new UserRole();
		userRole.setAccount(account);
		userRole.setRole(role);
		userRoleRepository.save(userRole);
		// Send confirmation email to admin
	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(roleBasedDetails.getSignUpRole().getEmail());
	    message.setFrom("abu053125@gmail.com");
	    message.setSubject("New user registration");
	    message.setText("Your registration request has been accepted with the following details:\n\n" 
                + "Email: " + roleBasedDetails.getSignUpRole().getEmail() 
                + "\n\n" + "You can now log in to your account using the following link:\n\n" 
                + "http://localhost:3000/login");

	    mailSender.send(message);

		if(roleBasedDetails.getStoreId() != null) {
			Store store = storeRepository.findById(roleBasedDetails.getStoreId())
					.orElseThrow(() -> new ResourceNotFoundException("Store", "id",
							roleBasedDetails.getSignUpRole().getRoleId().toString()));
			UserStore userStore = new UserStore();
			userStore.setAccount(account);
			userStore.setStore(store);
			userStoreRepository.save(userStore);
		}
		
		

		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				roleBasedDetails.getSignUpRole().getEmail(), roleBasedDetails.getSignUpRole().getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.createToken(authentication);
	
		return ResponseEntity.ok(new AuthResponse(token));
	}
	// get all request
	@GetMapping("/users")
    public ResponseEntity<List<SignUpRoleDto>> getroleUser() {
        List<SignUpRoleDto> acceptedProducts = signUpRoleService.getAcceptProducts();
        return ResponseEntity.ok(acceptedProducts);
    }
	
	// check if email is used or not
	  @GetMapping("/check-email")
	  public ResponseEntity<?> checkIfEmailIsUnique(@RequestParam String email) {
	    Optional<SignUpRole> sigOptional = signUpRoleRepository.findByEmail(email);
	    if (sigOptional.isPresent()) {
	      return ResponseEntity.badRequest().body("Email is already in use");
	    }
	    return ResponseEntity.ok().body("Email is available");
	  }
	
}
