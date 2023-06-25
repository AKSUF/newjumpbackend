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

import com.jumpstart.com.payloads.ApiResponse;
import com.jumpstart.com.payloads.DeliveryDetailsDto;
import com.jumpstart.com.service.DeliveryDetailsService;
import com.jumpstart.com.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/user")
public class DeliveryDetailsController {

	@Autowired
	private DeliveryDetailsService deliveryDetailsService;

	@Autowired
	private JwtUtils jwtUtils;

	// User will give his delivery details where the rider/deliveryman will deliver the order
	@PostMapping("/products/delivery_address")
	public ResponseEntity<DeliveryDetailsDto> addDeliverDetails(
			@Valid @RequestBody DeliveryDetailsDto deliveryDetailsDto, HttpServletRequest request) {

		DeliveryDetailsDto addDeliveryDetails = this.deliveryDetailsService.addDeliverDetails(deliveryDetailsDto,
				jwtUtils.getJWTFromRequest(request));

		return new ResponseEntity<DeliveryDetailsDto>(addDeliveryDetails, HttpStatus.CREATED);
	}

	// User can update his/her delivery-details
	@PutMapping("/products/delivery_address")
	public ResponseEntity<ApiResponse> updateAddedMeal(@Valid @RequestBody DeliveryDetailsDto deliveryDetailsDto,
			HttpServletRequest request) {

		this.deliveryDetailsService.updateDeliveryDetails(deliveryDetailsDto,  jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<ApiResponse>(new ApiResponse("Meal updated successfully", true), HttpStatus.CREATED);
	}
	
	
	// get all user delivery-details
	@GetMapping("/products/delivery_address")
	public ResponseEntity<List<DeliveryDetailsDto>> allDetailsByUser(HttpServletRequest request) {

		List<DeliveryDetailsDto> availableMeals = this.deliveryDetailsService.getDeliveryDetailsByUser(jwtUtils.getJWTFromRequest(request));

		if (availableMeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(availableMeals);
	}
	
	// Get user specific delivery details with id
	@GetMapping("/products/delivery_address/{ddid}")
	public ResponseEntity<DeliveryDetailsDto> getPartcularId(@PathVariable Long ddid) {
		return ResponseEntity.status(HttpStatus.OK).body(this.deliveryDetailsService.getDeliveryId(ddid));
	}
	
	// Delete user specific delivery details with id
	@DeleteMapping("/products/delivery_address/{ddid}")
	public ResponseEntity<ApiResponse> deleteDeliveryDetails(@PathVariable Long ddid) {
		this.deliveryDetailsService.deleteDeliveryDetails(ddid);
		;
		return new ResponseEntity<ApiResponse>(new ApiResponse("Store is deleted", true), HttpStatus.OK);

	}
}
