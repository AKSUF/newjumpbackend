package com.jumpstart.com.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
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
import com.jumpstart.com.entities.BuyProduct;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.exception.UnauthorizedException;
import com.jumpstart.com.payloads.ApiResponse;
import com.jumpstart.com.payloads.BuyProductDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.BuyProductRepository;
import com.jumpstart.com.service.BuyProductService;
import com.jumpstart.com.service.FileService;
import com.jumpstart.com.status.DeliveryStatus;
import com.jumpstart.com.status.RequestStatus;
import com.jumpstart.com.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/buy")
public class BuyProductController {
	@Autowired
	BuyProductService buyProductService;
	@Autowired
	BuyProductRepository buyProductRepository;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private FileService fileService;
	@Value("${project.buyProduct}")
	private String path;
	
	// shopkeeper can request the product from the manufacture/producer
	@PostMapping("/buy-product/{uid}")
	public ResponseEntity<BuyProductDto> addProductDetails(@Valid @RequestBody BuyProductDto buyProductDto,
			HttpServletRequest request, @PathVariable Long uid) {
		BuyProduct buyProduct = this.buyProductService.buyProduct(buyProductDto, uid,
				jwtUtils.getJWTFromRequest(request));
		BuyProductDto newProductDto = this.modelMapper.map(buyProduct, BuyProductDto.class);
		return new ResponseEntity<BuyProductDto>(newProductDto, HttpStatus.CREATED);
	}
	
	// Producer can see all his product requests
	@GetMapping("/producer-pending-product")
	public ResponseEntity<List<BuyProductDto>> allProducerProductRequest(HttpServletRequest request) {

		List<BuyProductDto> availableProduct = this.buyProductService
				.getProducerProducts(jwtUtils.getJWTFromRequest(request));

		if (availableProduct.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(availableProduct);
	}
	
	// Producer can see all his product accepted Product requests
	@GetMapping("/producer-request-accept-product")
	public ResponseEntity<List<BuyProductDto>> allProducerRequestAcceptProductRequest(HttpServletRequest request) {
		List<BuyProductDto> availableMeals = this.buyProductService
				.getProducerRequestAcceptedProducts(jwtUtils.getJWTFromRequest(request));
		if (availableMeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(availableMeals);
	}

	// Shopkeeper can see all his product requests
	@GetMapping("/shopkeeper-pending-product")
	public ResponseEntity<List<BuyProductDto>> allStorePendingProducts(HttpServletRequest request) {

		List<BuyProductDto> availableMeals = this.buyProductService
				.getShopkeeperProductBuy(jwtUtils.getJWTFromRequest(request));

		if (availableMeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(availableMeals);
	}

	// get particular buy Product
	@GetMapping("/buy-products/{bpid}")
	public ResponseEntity<BuyProductDto> getAccountInfo(@PathVariable Long bpid) {
		return ResponseEntity.status(HttpStatus.OK).body(this.buyProductService.getProduct(bpid));
	}
	
	
	// Shopkeeper can update his/her buy-product
	@PutMapping("/buy-products")
	public ResponseEntity<ApiResponse> updateBuyProducttDetails(@Valid @RequestBody BuyProductDto buyProductDto,
			HttpServletRequest request) {
		this.buyProductService.updateBuyProducttDetails(buyProductDto, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<ApiResponse>(new ApiResponse("Meal updated successfully", true), HttpStatus.CREATED);
	}


	// Producer can accept shopkeeper product
	@PutMapping("/accept-product/{bpid}")
	public ResponseEntity<?> acceptRquest(@PathVariable Long bpid, HttpServletRequest request,
			@RequestBody Map<String, Object> requestBody) {
		String token = getJWTFromRequest(request);
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email).get();
		User user = account.getUser();
		BuyProduct buyProduct = this.buyProductRepository.findById(bpid)
				.orElseThrow(() -> new ResourceNotFoundException("BuyProduct", "Id", bpid.toString()));

		if (buyProduct.getProducerId() != user.getUser_id()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("You are not authorized to accept this product.");
		}

		buyProduct.setAccepted(true);
		buyProduct.setMessage((String) requestBody.get("message"));
		buyProduct.setPrice((int) requestBody.get("price"));
		buyProduct.setStatus(RequestStatus.REQUEST_ACCEPTED.name());
		buyProduct.setDeliveryStatus(DeliveryStatus.PENDING.name());
		buyProduct.setTotalPrice(buyProduct.getPrice() * buyProduct.getHowMuch());
		buyProductRepository.save(buyProduct);

		return ResponseEntity.ok().build();
	}
	
	//If the producer does not have the product, he can reject the product
	@PutMapping("/not-available-product/{bpid}")
	public ResponseEntity<?> markProductAsUnavailable(@PathVariable Long bpid, HttpServletRequest request,
			@RequestBody String message) {
		try {
			String token = getJWTFromRequest(request);
			String email = jwtUtils.getUserNameFromToken(token);
			Account account = accountRepository.findByEmail(email)
					.orElseThrow(() -> new ResourceNotFoundException("Account", "Email", email));
			User user = account.getUser();
			BuyProduct buyProduct = buyProductRepository.findById(bpid)
					.orElseThrow(() -> new ResourceNotFoundException("BuyProduct", "Id", bpid.toString()));

			if (!buyProduct.getProducerId().equals(user.getUser_id())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("You are not authorized to mark this product as unavailable.");
			}

			buyProduct.setAccepted(false);
			buyProduct.setMessage(message);
			buyProduct.setDeliveryStatus("");
			buyProduct.setStatus(RequestStatus.NOT_AVAILABLE.name());
			buyProductRepository.save(buyProduct);

			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing the request.");
		}
	}
	
	// Shopkeeper can remove/delete his/her buy product 
	@DeleteMapping("/remove/{bpid}")
	public ResponseEntity<ApiResponse> deleteBuyProductRequest(@PathVariable Long bpid, HttpServletRequest request) {
		try {
			buyProductService.deleteBuyProductRequest(bpid, jwtUtils.getJWTFromRequest(request));
			return new ResponseEntity<ApiResponse>(new ApiResponse("BuyProduct deleted successfully", true),
					HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(), false), HttpStatus.UNAUTHORIZED);
		} catch (IOException e) {
			return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(), false),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}

		return null;
	}

	// uploading meal image
	@PostMapping("/buy-products/{bpid}/upload-buy-product-image")
	public ResponseEntity<ApiResponse> uploadFile(@PathVariable("bpid") Long bpid,
			@RequestParam("file") MultipartFile image) throws IOException {

		// insuring the request has a file
		if (image.isEmpty()) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("Request must have a file", false),
					HttpStatus.BAD_REQUEST);
		}

		// uploading the file into server
		this.buyProductService.uploadImage(image, bpid);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Product image uploaded successfully", true),
				HttpStatus.OK);
	}

	// method to serve products image
	@GetMapping(value = "/buy-products/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable String imageName, HttpServletResponse response) throws IOException {
		InputStream resource = this.fileService.getResource(path, imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}

	@PutMapping("/producer/delivery-status/{bpid}")
	public ResponseEntity<BuyProductDto> orderMeal(HttpServletRequest request, @PathVariable Long bpid) {
		BuyProductDto setStatus = this.buyProductService.shopkeeperProductDeliveryStatus(bpid,
				jwtUtils.getJWTFromRequest(request));

		return new ResponseEntity<BuyProductDto>(setStatus, HttpStatus.OK);
	}

}
