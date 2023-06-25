package com.jumpstart.com.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.Product;
import com.jumpstart.com.payloads.ProductDto;
import com.jumpstart.com.payloads.ProductResponse;

public interface ProductService {
	   // add Product Details
//		Product addProductDetails(ProductDto productDto, String token, Long sid);

			// upload Product image
			void uploadImage(MultipartFile multipartFile, Long mid) throws IOException;

			// delete Product
			void deleteSingleProduct(Long pid) throws IOException;

			// update Product
			ProductDto updateProductDetails(ProductDto productDto, String token);

			// get Product with pagination
			ProductResponse getAllProducts(Long pageNumber, Long pageSize, String sortBy, String sortDir);

			// get all Products
			List<ProductDto> getApproveProducts();

			// get particular Product
			ProductDto getProduct(Long pid);
			

			List<ProductDto> searchProducts(String keyword);

			

			String getAdminMessageForProduct(Long productId);

			List<ProductDto> getPendingProducts();

			Product getProductByIdWithoutGet(Long id);

			Product addProductDetails(ProductDto productDto, String token);

			List<ProductDto> getAllStoreDetails(String token);

			List<ProductDto> getAllProducts(String token);
}
