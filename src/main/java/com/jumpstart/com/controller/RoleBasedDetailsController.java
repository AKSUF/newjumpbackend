package com.jumpstart.com.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.Role;
import com.jumpstart.com.entities.RoleBasedDetails;
import com.jumpstart.com.entities.SignUpRole;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.ApiResponse;
import com.jumpstart.com.payloads.RoleBasedDetailsDto;
import com.jumpstart.com.repository.RoleBasedDetailsRepository;
import com.jumpstart.com.repository.SignUpRoleRepository;
import com.jumpstart.com.service.FileService;
import com.jumpstart.com.service.RoleBasedDetailsService;

@RestController
@RequestMapping("/api/v1/unauthorize")
public class RoleBasedDetailsController {
	@Autowired
	private RoleBasedDetailsService roleBasedDetailsService;
	@Autowired
	private RoleBasedDetailsRepository roleBasedDetailsRepository;
	@Autowired
	private FileService fileService;
	@Autowired
	private SignUpRoleRepository signUpRoleRepository;
	@Autowired
	private JavaMailSender mailSender;
	@PersistenceContext
	private EntityManager entityManager;

	@Value("${project.image}")
	private String path;

	// if anyone want to register as employee, shopkeeper, rider and producer he/she
	// need to register (with email, password and role)
	@PostMapping("/register")
	public ResponseEntity<RoleBasedDetailsDto> registerRolrLocal(
			@Valid @RequestBody RoleBasedDetailsDto roleBasedDetailsDto, @RequestParam String token) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(roleBasedDetailsService.createToleProfile(roleBasedDetailsDto, token));
	}

	// using token we will get specific user details
	@GetMapping("/user")
	public ResponseEntity<RoleBasedDetailsDto> getAccountInfo(@RequestParam String token) {
		return ResponseEntity.status(HttpStatus.OK).body(this.roleBasedDetailsService.getUserRoleDetails(token));
	}

	// After selecting the email, password, role, the user has to provide some
	// information
	@PostMapping("/register/{srid}/{sid}")
	public ResponseEntity<RoleBasedDetailsDto> createShopkeerperProfile(
			@Valid @RequestBody RoleBasedDetailsDto roleBasedDetailsDto, @PathVariable Long srid,
			@PathVariable Long sid) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(roleBasedDetailsService.createShopkeerperProfile(roleBasedDetailsDto, srid, sid));
	}

	// image post
	@PostMapping("/image/upload-profile-image/{srid}")
	public ResponseEntity<ApiResponse> uploadFile(@PathVariable("srid") Long srid,
			@RequestParam("file") MultipartFile image) throws IOException {

		// insuring the request has a file
		if (image.isEmpty()) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("Request must have a file", false),
					HttpStatus.BAD_REQUEST);
		}
		// uploading the file into server
		this.roleBasedDetailsService.uploadImage(image, srid);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Profile image uploaded successfully", true),
				HttpStatus.OK);

	}

	@GetMapping(value = "/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable String imageName, HttpServletResponse response) throws IOException {
		InputStream resource = this.fileService.getResource(path, imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}

	@GetMapping("/check-jumpStartId")
	public ResponseEntity<?> checkIfJumpStartIdsUnique(@RequestParam String jumpStartId) {
		Optional<RoleBasedDetails> sigOptional = roleBasedDetailsRepository.findByJumpStartId(jumpStartId);
		if (sigOptional.isPresent()) {
			return ResponseEntity.badRequest().body("Jumpstart Employee Id is already in use");
		}
		return ResponseEntity.ok().body("OK");
	}

	// get all Users
	@GetMapping("/users")
	public ResponseEntity<List<RoleBasedDetailsDto>> allUsers() {

		List<RoleBasedDetailsDto> users = this.roleBasedDetailsService.getAllUserRequest();

		if (users.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(users);
	}

	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}

		return null;
	}

	// Admin can reject user
	@DeleteMapping("/users")
	public ResponseEntity<?> markProductAsUnavailable(@RequestParam String token) {

		RoleBasedDetails roleBasedDetails = this.roleBasedDetailsRepository.findByToken(token)
				.orElseThrow(() -> new ResourceNotFoundException("Role Based Details", "Role Based Details id", token));
		SignUpRole signUpRole = this.signUpRoleRepository.findByToken(token);
//		        roleBasedDetails.setAdminMessage(message);
		roleBasedDetailsRepository.save(roleBasedDetails);
		SimpleMailMessage messages = new SimpleMailMessage();
		messages.setTo(roleBasedDetails.getSignUpRole().getEmail());

		messages.setFrom("abu053125@gmail.com");
		messages.setSubject("Registration Request Rejected");

		Long roleId = roleBasedDetails.getSignUpRole().getRoleId(); // Get the first role from the user roles
		Role role = entityManager.find(Role.class, roleId);
		String roleName = role.getRole_name();

		messages.setText("Dear " + roleBasedDetails.getName() + ",\n\n" + "Thank you for registering as a " + roleName
				+ ".\n\n"
				+ "Unfortunately, we are unable to approve your registration request at this time due to the following reason:\n\n"
				+ "\n\nWe hope that you will not be discouraged by this decision and that you will continue to explore other opportunities to engage with our organization.\n\n"
				+ "If you have any questions or concerns about this decision, please do not hesitate to contact us at [contact information].\n\n"
				+ "Sincerely,\n\n" + "[Your Name]\n" + "[Your Organization]");
		mailSender.send(messages);
		this.roleBasedDetailsRepository.delete(roleBasedDetails);
		this.signUpRoleRepository.delete(signUpRole);

		return ResponseEntity.ok().build();
	}
}
