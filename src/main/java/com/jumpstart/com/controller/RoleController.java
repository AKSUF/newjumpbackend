package com.jumpstart.com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Role;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.entities.UserRole;
import com.jumpstart.com.payloads.RoleDto;
import com.jumpstart.com.payloads.RolesArrayResponse;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.RoleRepository;
import com.jumpstart.com.repository.UserRepository;
import com.jumpstart.com.repository.UserRoleRepository;
import com.jumpstart.com.service.RoleService;
import com.jumpstart.com.service.UserService;
import com.jumpstart.com.utils.JwtUtils;
@RestController 
@RequestMapping("/api/v1/role")
public class RoleController {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private RoleService roleService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private UserService userService;

	// If the user is logged in, we can know his role
	@GetMapping("/userRoles")
	public ResponseEntity<?> getUserRoles(HttpServletRequest request) {
		String token = jwtUtils.getJWTFromRequest(request);
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email).get();
		List<String> roleResponses = new ArrayList<String>();
		account.getUserRoles().stream().forEach((role) -> {
			roleResponses.add(role.getRole().getRole_name());
		});
		return ResponseEntity.ok(new RolesArrayResponse(roleResponses));
	}

	// assignRole
	@PostMapping("/{userId}")
	public ResponseEntity<?> appendNewRole(HttpServletRequest request, @PathVariable Long userId,
			@RequestParam String assignRoles) {
		User user = userService.getUser(userId);
		if (user == null) {
			return new ResponseEntity<String>("User with " + userId + " cannot be found", HttpStatus.BAD_REQUEST);
		}
		Account account = user.getAccount();
		List<UserRole> originalUserRole = account.getUserRoles();
		originalUserRole.forEach((userRole) -> {
			userRoleRepository.deleteById(userRole.getUser_role_id());
		});
		String[] roles = assignRoles.split(",");
		for (String roleId : roles) {
			Role role = roleRepository.findById(Long.parseLong(roleId)).get();
			UserRole userRole = new UserRole();
			userRole.setAccount(account);
			userRole.setRole(role);
			userRoleRepository.save(userRole);
		}
		return new ResponseEntity<String>("User roles have been updated successfully", HttpStatus.OK);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<?> getRolesForOtherUser(HttpServletRequest request, @PathVariable Long userId) {
		User user = userService.getUser(userId);
		if (user == null) {
			return new ResponseEntity<String>("User with " + userId + " cannot be found", HttpStatus.BAD_REQUEST);
		}
		Account account = user.getAccount();
		List<String> roleResponses = new ArrayList<String>();
		account.getUserRoles().stream().forEach((role) -> {
			roleResponses.add(role.getRole().getRole_name());
		});
		return ResponseEntity.ok(new RolesArrayResponse(roleResponses));
	}

	// get Role Producer by id and email
	@GetMapping("/producers")
	public ResponseEntity<?> getProducers(HttpServletRequest request) {
	    List<User> users = userRepository.findAll();
	    List<Map<String, Object>> producerList = new ArrayList<>();
	    users.stream()
	            .filter(user -> user.getAccount() != null && user.getAccount().getUserRoles() != null)
	            .filter(user -> user.getAccount().getUserRoles().stream()
	                    .anyMatch(userRole -> userRole.getRole() != null && userRole.getRole().getRole_name() != null && userRole.getRole().getRole_name().equals("ROLE_PRODUCER")))
	            .forEach(user -> {
	                Map<String, Object> producer = new HashMap<>();
	                producer.put("id", user.getUser_id());
	                producer.put("email", user.getAccount().getEmail());
	                producerList.add(producer);
	            });
	    return ResponseEntity.ok(producerList);
	}
	
	// Get all roles except user, admin, shopkeeper
	@GetMapping("/roles")
	public ResponseEntity<List<RoleDto>> getRoles() {
		List<RoleDto> roleDtos = this.roleService.getRoles();
		List<RoleDto> filteredRoles = roleDtos.stream()
				.filter(role -> !role.getRole_name().equals("ROLE_USER") && !role.getRole_name().equals("ROLE_ADMIN")
						&& !role.getRole_name().equals("ROLE_SHIPPING_COURIER"))
				.filter(role -> role.getRole_name().startsWith("ROLE_")).collect(Collectors.toList());
		return ResponseEntity.ok(filteredRoles);
	}

	
	@GetMapping("/store/{userId}")
	public ResponseEntity<?> getStoreForOtherUser(HttpServletRequest request, @PathVariable Long userId) {
		User user = userService.getUser(userId);
		if (user == null) {
			return new ResponseEntity<String>("User with " + userId + " cannot be found", HttpStatus.BAD_REQUEST);
		}
		Account account = user.getAccount();
		List<String> roleResponses = new ArrayList<String>();
		account.getUserStores().stream().forEach((role) -> {
			roleResponses.add(String.valueOf(role.getStore().getStore_id()));
		});
		return ResponseEntity.ok(new RolesArrayResponse(roleResponses));
	}

	// get all riders
	@GetMapping("/riders")
	public ResponseEntity<?> getRiders(HttpServletRequest request) {
		List<User> users = userRepository.findAll();
		List<Map<String, Object>> producerList = new ArrayList<>();
		users.stream().filter(user -> user.getAccount().getUserRoles().stream()
				.anyMatch(userRole -> userRole.getRole().getRole_name().equals("ROLE_RIDER"))).forEach(user -> {
					Map<String, Object> producer = new HashMap<>();
					producer.put("id", user.getUser_id());
					producer.put("email", user.getAccount().getEmail());
					producer.put("name", user.getAccount().getUser().getName());
					producer.put("district", user.getAccount().getUser().getDistrict());
					producer.put("address", user.getAccount().getUser().getAddress());
					producer.put("Types of vehicles", user.getAccount().getUser().getTypeOfVehicles());
					producerList.add(producer);
				});
		return ResponseEntity.ok(producerList);
	}

	@GetMapping("/count/user")
	public Long countUserRole() {
		return roleRepository.countUserRole();
	}

	@GetMapping("/count/rider")
	public Long countRiderRole() {
		return roleRepository.countRiderRole();
	}
	@GetMapping("/count/emp")
	public Long countEmployeeRole() {
		return roleRepository.countEmployeeRole();
	}
}
