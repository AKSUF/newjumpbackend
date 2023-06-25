package com.jumpstart.com.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.SetShiftsForRiders;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.payloads.ApiResponse;
import com.jumpstart.com.payloads.DeliveryDto;
import com.jumpstart.com.payloads.SetShiftsForRidersDto;
import com.jumpstart.com.service.AccountService;
import com.jumpstart.com.service.DeliveryService;
import com.jumpstart.com.service.SetShiftsForRiderService;
import com.jumpstart.com.utils.JwtUtils;
@RestController 
@RequestMapping("/api/v1/rider")
public class ShiftsFirRidersController {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private SetShiftsForRiderService setShiftsForRidersService;
	@Autowired
	private DeliveryService deliveryService;
	@Autowired
	private AccountService accountService;

	/* Rider Based Code */
	
	// Get all available district based rider shifts
	@GetMapping("/shifts")
	public ResponseEntity<List<SetShiftsForRidersDto>> getAllShiftsForRider(HttpServletRequest request) {
		List<SetShiftsForRidersDto> setShiftsForRidersDtos = setShiftsForRidersService
				.getAllShiftsForRider(jwtUtils.getJWTFromRequest(request));
		return ResponseEntity.ok().body(setShiftsForRidersDtos);
	}

	// Get all available district based rider id
	@GetMapping("/shifts/rider")
	public ResponseEntity<List<SetShiftsForRidersDto>> getAllShiftsbasedRiderId(HttpServletRequest request) {
		List<SetShiftsForRidersDto> setShiftsForRidersDtos = setShiftsForRidersService
				.getAllShiftsbasedRiderId(jwtUtils.getJWTFromRequest(request));
		return ResponseEntity.ok().body(setShiftsForRidersDtos);
	}

	// rider can take available shift
	@PutMapping("/shift/taken")
	public ResponseEntity<SetShiftsForRidersDto> takeShift(HttpServletRequest request,
			@RequestParam String shifttoken) {
		SetShiftsForRidersDto setStatus = this.setShiftsForRidersService.shiftStatusTaken(shifttoken,
				jwtUtils.getJWTFromRequest(request));

		return new ResponseEntity<SetShiftsForRidersDto>(setStatus, HttpStatus.OK);
	}

	// rider can Swap his / her shift
	@PutMapping("/shift/offer-swap")
	public ResponseEntity<SetShiftsForRidersDto> offerSwap(HttpServletRequest request,
			@RequestParam String shifttoken) {
		SetShiftsForRidersDto setStatus = this.setShiftsForRidersService.shiftStatusOfferSwap(shifttoken,
				jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<SetShiftsForRidersDto>(setStatus, HttpStatus.OK);
	}

	@GetMapping("/{deliveryId}/check-districts")
	public ResponseEntity<String> checkDistrictsSame(@PathVariable Long deliveryId, HttpServletRequest request) {
		boolean districtsSame = deliveryService.checkDistrictsSame(deliveryId, jwtUtils.getJWTFromRequest(request));
		if (districtsSame) {
			return ResponseEntity.ok("Districts are the same");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Districts are not the same");
		}
	}

	// district based delivery can accept
	@GetMapping("/rider-orders")
	public ResponseEntity<List<DeliveryDto>> getAllDeliveries(HttpServletRequest request) {
		List<DeliveryDto> deliveryDtos = deliveryService.getAllDeliveriesByRider(jwtUtils.getJWTFromRequest(request));
		return ResponseEntity.ok(deliveryDtos);
	}
	
	/* Shipping Courier Code */

	//If the district is different the orders will go to the courier
	@GetMapping("/courier")
	public ResponseEntity<List<DeliveryDto>> getAllNotMatchedDeliveriesByCourier() {
		List<DeliveryDto> notMatchedDeliveries = deliveryService.getAllDeliveriesByCourier();
		return ResponseEntity.ok(notMatchedDeliveries);
	}
	
	 // If the user district and product shipping address is the same, the order will come to the rider 
	@GetMapping("/rider-emp")
	public ResponseEntity<List<DeliveryDto>> getAllDeliveriesByEmployee(HttpServletRequest request) {
		List<DeliveryDto> notMatchedDeliveries = deliveryService
				.getAllDeliveriesByEmployee(jwtUtils.getJWTFromRequest(request));
		return ResponseEntity.ok(notMatchedDeliveries);
	}

	// Rider or courier can change his delivery status
	@PutMapping("/products/{deliveryId}")
	public ResponseEntity<DeliveryDto> orderMeal(HttpServletRequest request, @PathVariable Long deliveryId,
			@RequestParam String status) {
		String token = jwtUtils.getJWTFromRequest(request);
		Account account = accountService.getAccount(token);
		User user = account.getUser();
		DeliveryDto orderedMeal = this.deliveryService.orderProductStatus(user, deliveryId, status);

		return new ResponseEntity<DeliveryDto>(orderedMeal, HttpStatus.OK);
	}

	// Courier can change his delivery status
	@PutMapping("/products/courier/{deliveryId}")
	public ResponseEntity<DeliveryDto> orderProduct(HttpServletRequest request, @PathVariable Long deliveryId,
			@RequestParam String status) {

		String token = jwtUtils.getJWTFromRequest(request);
		Account account = accountService.getAccount(token);
		User user = account.getUser();
		DeliveryDto orderedMeal = this.deliveryService.orderProductStatusForCourier(user, deliveryId, status);

		return new ResponseEntity<DeliveryDto>(orderedMeal, HttpStatus.OK);
	}
	

	// Employee sent request to rider to deliver the order
    @PutMapping("/{deliveryId}/request/{riderId}")
    public ResponseEntity<Object> employeeRequestForDelivery(@PathVariable Long deliveryId, @PathVariable Long riderId) throws Exception {
        deliveryService.employeeRequestForDeliver(deliveryId, riderId);;
        return ResponseEntity.ok().build();
    }

}
