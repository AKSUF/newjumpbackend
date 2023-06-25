package com.jumpstart.com.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jumpstart.com.payloads.AddtoCartDto;
import com.jumpstart.com.payloads.ApiResponse;
import com.jumpstart.com.payloads.CommentDto;
import com.jumpstart.com.service.CartService;
import com.jumpstart.com.service.CommentService;
import com.jumpstart.com.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/user/")
public class CartController {

	@Autowired
	CartService cartService;
	@Autowired
	private JwtUtils jwtUtils;
	
	
	// User can add the product to the cart
	@PostMapping("/addToCart/{pid}/product")
	public ResponseEntity<?> addToCart(
			@PathVariable Long pid,
			HttpServletRequest request) throws Exception{
		AddtoCartDto addToCart = this.cartService.addCartbyUserIdAndProductId( pid, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<AddtoCartDto>(addToCart, HttpStatus.CREATED);
	}
	
	// User can delete/remove the cart 
	@DeleteMapping("/{cartId}/removeCart")
	public  ResponseEntity<ApiResponse> deleteCart(@PathVariable Long cartId, HttpServletRequest request) throws Exception {
		this.cartService.removeCart(cartId, jwtUtils.getJWTFromRequest(request));
		return new  ResponseEntity<ApiResponse>(new ApiResponse("Cart is successfully deleted", true),HttpStatus.OK);

	}
	
	
	// User will get all his cart together 
	@GetMapping("/carts")
	public ResponseEntity<List<AddtoCartDto>> allCart(HttpServletRequest request) {
		List<AddtoCartDto> availableMeals = this.cartService.getCartByUserId(jwtUtils.getJWTFromRequest(request));

		if (availableMeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(availableMeals);
	}
	
	// User can increase product quantity
	@PutMapping("/carts/{cid}/{qty}")
	public ResponseEntity<ApiResponse> updateQuantity(@PathVariable Long cid,
			@PathVariable int qty) throws Exception {
		this.cartService.updateQtyByCartId( cid, qty);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Meal updated successfully", true), HttpStatus.CREATED);
	}
	
	
	 
	
}
