package com.jumpstart.com.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Store;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.entities.UserRole;
import com.jumpstart.com.entities.UserStore;
import com.jumpstart.com.payloads.AccountDto;
import com.jumpstart.com.payloads.ApiResponse;
import com.jumpstart.com.payloads.RolesArrayResponse;
import com.jumpstart.com.payloads.StoreDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.StoreRepository;
import com.jumpstart.com.repository.UserStoreRepository;
import com.jumpstart.com.service.FileService;
import com.jumpstart.com.service.StoreService;
import com.jumpstart.com.service.UserService;
import com.jumpstart.com.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/admin")
public class StoreController {
	@Autowired
	public StoreService storeService;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private FileService fileService;
	@Value("${project.store}")
	private String path;

	@Autowired
	private UserStoreRepository userStoreRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private UserService userService;

	// shopkeeper can add product
	@PostMapping("/store")
	public ResponseEntity<StoreDto> createCategory(@Valid @RequestBody StoreDto storeDto, HttpServletRequest request) {
		StoreDto createCategory = this.storeService.addStore(storeDto, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<StoreDto>(createCategory, HttpStatus.CREATED);
	}

	// shopkeeper can edit product
	@PutMapping("/store")
	public ResponseEntity<StoreDto> updateCategory(@Valid @RequestBody StoreDto storeDto, HttpServletRequest request) {
		StoreDto updateStoreDto = this.storeService.updateStore(storeDto, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<StoreDto>(updateStoreDto, HttpStatus.OK);
	}

	// shopkeeper can delete his/her product
	@DeleteMapping("/store/{sid}")
	public ResponseEntity<ApiResponse> deleteStore(@PathVariable Long sid) {
		this.storeService.deleteStore(sid);
		;
		return new ResponseEntity<ApiResponse>(new ApiResponse("Store is deleted", true), HttpStatus.OK);

	}

	// get specific store by id
	@GetMapping("/store/{sid}")
	public ResponseEntity<StoreDto> getStore(@PathVariable Long sid) {
		StoreDto storeDto = this.storeService.getStore(sid);
		return new ResponseEntity<StoreDto>(storeDto, HttpStatus.OK);

	}

	// get all Store details
	@GetMapping("/store")
	public ResponseEntity<List<StoreDto>> getAllStores() {
		List<StoreDto> storeDtos = this.storeService.getAllStoreDetails();
		return new ResponseEntity<List<StoreDto>>(storeDtos, HttpStatus.OK);

	}

	// uploading store image
	@PostMapping("/store/{sid}/upload-store-image")
	public ResponseEntity<ApiResponse> uploadFile(@PathVariable("sid") Long sid,
			@RequestParam("file") MultipartFile image) throws IOException {

		// insuring the request has a file
		if (image.isEmpty()) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("Request must have a file", false),
					HttpStatus.BAD_REQUEST);
		}

		// uploading the file into server
		this.storeService.uploadImage(image, sid);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Store image uploaded successfully", true),
				HttpStatus.OK);
	}

	// method to serve user profile image
	@GetMapping(value = "/store/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable String imageName, HttpServletResponse response) throws IOException {
		InputStream resource = this.fileService.getResource(path, imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}

	@GetMapping("/userStores")
	public ResponseEntity<?> getUserStores(HttpServletRequest request) {
		String token = jwtUtils.getJWTFromRequest(request);
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email).get();
		List<String> storeResponses = new ArrayList<String>();
		account.getUserStores().stream().forEach((store) -> {
			storeResponses.add(store.getStore().getStore_name());
		});
		return ResponseEntity.ok(new RolesArrayResponse(storeResponses));
	}

	@PostMapping("/{userId}")
	public ResponseEntity<?> appendNewRole(HttpServletRequest request, @PathVariable Long userId,
			@RequestParam String assignStores) {
		User user = userService.getUser(userId);
		if (user == null) {
			return new ResponseEntity<String>("User with " + userId + " cannot be found", HttpStatus.BAD_REQUEST);
		}
		Account account = user.getAccount();
		List<UserRole> originalUserRole = account.getUserRoles();
		originalUserRole.forEach((userRole) -> {
			userStoreRepository.deleteById(userRole.getUser_role_id());
		});
		String[] stores = assignStores.split(",");
		for (String storeId : stores) {
			Store store = storeRepository.findById(Long.parseLong(storeId)).get();
			UserStore userStore = new UserStore();
			userStore.setAccount(account);
			userStore.setStore(store);
			;
			userStoreRepository.save(userStore);
		}
		return new ResponseEntity<String>("User Store have been updated successfully", HttpStatus.OK);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<?> getStoresForOtherUser(HttpServletRequest request, @PathVariable Long userId) {
		User user = userService.getUser(userId);
		if (user == null) {
			return new ResponseEntity<String>("User with " + userId + " cannot be found", HttpStatus.BAD_REQUEST);
		}
		Account account = user.getAccount();
		List<String> storeResponses = new ArrayList<String>();
		account.getUserStores().stream().forEach((store) -> {
			storeResponses.add(store.getStore().getStore_name());
		});
		return ResponseEntity.ok(new RolesArrayResponse(storeResponses));
	}

	@GetMapping("/stores")
	public ResponseEntity<?> getProducers(HttpServletRequest request) {
		List<Store> storeDtos = storeRepository.findAll();
		List<Map<String, Object>> storeList = new ArrayList<>();
		storeDtos.forEach(store -> {
			Map<String, Object> storeData = new HashMap<>();
			storeData.put("id", store.getStore_id());
			storeData.put("name", store.getStore_name());
			storeList.add(storeData);
		});
		return ResponseEntity.ok(storeList);
	}

	@GetMapping("/{storeId}/accounts")
	public ResponseEntity<List<AccountDto>> getAccountsByStoreId(@PathVariable Long storeId) {
		List<AccountDto> accounts = storeService.getAccountsByStoreId(storeId);
		if (accounts == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(accounts);
	}

	@GetMapping("/stores/count")
	public Long countStores() {
		return storeRepository.countStores();
	}
}
