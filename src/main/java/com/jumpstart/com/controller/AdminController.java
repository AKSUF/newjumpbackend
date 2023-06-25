package com.jumpstart.com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jumpstart.com.entities.Product;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.ProductDto;
import com.jumpstart.com.repository.ProductRepository;
import com.jumpstart.com.service.ProductService;
import com.jumpstart.com.status.AdminStatus;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
	@Autowired
    private ProductService productService;
	@Autowired
	private ProductRepository productRepository;
	
	// admin can approve shopkeeper's product
    @PutMapping("/approve-product/{productId}")
    public ResponseEntity<?> approveProduct(@PathVariable Long productId) {
        Product product = productRepository.findById(productId).get();
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        product.setApproved(true);
        product.setAdminStatus(AdminStatus.APPROVE.name());
        productRepository.save(product);
        return ResponseEntity.ok().build();
    }
    
    // admin can send messages without rejection
    @PostMapping("/sendAdminMessage/{productId}")
    public ResponseEntity<?> sendAdminMessage(@PathVariable Long productId, @RequestBody String message) {
        Product optionalProduct = productRepository.findById(productId).get();
        if (optionalProduct == null) {
            return ResponseEntity.notFound().build();
        }       
        optionalProduct.setAdminMessage(message);
        productRepository.save(optionalProduct);
        return ResponseEntity.ok().build();
    }
    
    // Admin can reject with message
    @PutMapping("/reject-product/{productId}")
    public ResponseEntity<?> rejectProduct(@PathVariable Long productId, @RequestBody String adminMessage) {
        Product product = productRepository.findById(productId).get();

        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        product.setApproved(false);
        product.setAdminStatus(AdminStatus.REJECTED.name());
        product.setAdminMessage(adminMessage);
        productRepository.save(product);

        return ResponseEntity.ok().build();
    }
    // to get the message which is given by admin
    @GetMapping("/products/{id}/admin-message")
    public ResponseEntity<String> getAdminMessageForProduct(@PathVariable("id") Long id) {
        try {
            String adminMessage = productService.getAdminMessageForProduct(id);
            return ResponseEntity.ok(adminMessage);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // shopkeeper and admin can view all pending projects
	@GetMapping("/pending-products")
	public ResponseEntity<List<ProductDto>> allProjuct() {
		List<ProductDto> availableMeals = this.productService.getPendingProducts();
		if (availableMeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(availableMeals);
	}
}
