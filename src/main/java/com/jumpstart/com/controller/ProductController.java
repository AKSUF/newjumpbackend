package com.jumpstart.com.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

import com.jumpstart.com.config.AppConstants;
import com.jumpstart.com.entities.Product;
import com.jumpstart.com.payloads.ApiResponse;
import com.jumpstart.com.payloads.ProductDto;
import com.jumpstart.com.payloads.ProductResponse;
import com.jumpstart.com.service.FileService;
import com.jumpstart.com.service.ProductService;
import com.jumpstart.com.utils.JwtUtils;
@RestController 
@RequestMapping("/api/v1/producer")
public class ProductController {
	@Autowired
	private ProductService productService;

	@Autowired
	private FileService fileService;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private ModelMapper modelMapper;

	@Value("${project.product}")
	private String path;

	// add new product
	@PostMapping("/products")
	public ResponseEntity<ProductDto> addProductDetails(@Valid @RequestBody ProductDto productDto,
			HttpServletRequest request) {
		Product product = this.productService.addProductDetails(productDto, jwtUtils.getJWTFromRequest(request));
		ProductDto newProductDto = this.modelMapper.map(product, ProductDto.class);
		return new ResponseEntity<ProductDto>(newProductDto, HttpStatus.CREATED);
	}

	// update added Product
	@PutMapping("/products")
	public ResponseEntity<ApiResponse> updateAddedMeal(@Valid @RequestBody ProductDto productDto,
			HttpServletRequest request) {
		System.out.println(productDto.getStatus());
		this.productService.updateProductDetails(productDto, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<ApiResponse>(new ApiResponse("Meal updated successfully", true), HttpStatus.CREATED);
	}

	// delete added Product
	@DeleteMapping("/products/{pid}")
	public ResponseEntity<ApiResponse> deleteAccount(@PathVariable Long pid) throws IOException {
		this.productService.deleteSingleProduct(pid);
		;
		return new ResponseEntity<ApiResponse>(new ApiResponse("Meal deleted successfully", true), HttpStatus.OK);
	}

	// get particular Product
	@GetMapping("/products/{pid}")
	public ResponseEntity<ProductDto> getAccountInfo(@PathVariable Long pid) {
		return ResponseEntity.status(HttpStatus.OK).body(this.productService.getProduct(pid));
	}

	// get all Products
	@GetMapping("/products")
	public ResponseEntity<List<ProductDto>> allMeals() {

		List<ProductDto> availableMeals = this.productService.getApproveProducts();

		if (availableMeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(availableMeals);
	}

	@GetMapping("/product-pending/store/")
	public ResponseEntity<List<ProductDto>> allStorePendingProducts() {

		List<ProductDto> availableMeals = this.productService.getPendingProducts();

		if (availableMeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(availableMeals);
	}

	@GetMapping("/stores/products")
	public ResponseEntity<List<ProductDto>> getAllProductsForUserStores(HttpServletRequest request) {
		List<ProductDto> products = productService.getAllStoreDetails(jwtUtils.getJWTFromRequest(request));
		return ResponseEntity.ok().body(products);
	}

	// get meals with pagination
	@GetMapping("/all-products")
	public ResponseEntity<ProductResponse> getAllPost(
			@RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Long pageNumber,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Long pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

		ProductResponse productResponse = this.productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir);

		return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.OK);
	}

	// uploading meal image
	@PostMapping("/products/{pid}/upload-product-image")
	public ResponseEntity<ApiResponse> uploadFile(@PathVariable("pid") Long pid,
			@RequestParam("file") MultipartFile image) throws IOException {

		// insuring the request has a file
		if (image.isEmpty()) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("Request must have a file", false),
					HttpStatus.BAD_REQUEST);
		}

		// uploading the file into server
		this.productService.uploadImage(image, pid);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Product image uploaded successfully", true),
				HttpStatus.OK);
	}

	// method to serve products image
	@GetMapping(value = "/products/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable String imageName, HttpServletResponse response) throws IOException {
		InputStream resource = this.fileService.getResource(path, imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}

	@GetMapping("/msg")
	public List<ProductDto> getAllProducts(HttpServletRequest request) {
		return productService.getAllProducts(jwtUtils.getJWTFromRequest(request));
	}
}
