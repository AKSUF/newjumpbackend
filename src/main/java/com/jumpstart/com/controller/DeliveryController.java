package com.jumpstart.com.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jumpstart.com.entities.Product;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.DeliveryDto;
import com.jumpstart.com.repository.ProductRepository;
import com.jumpstart.com.service.DeliveryService;
import com.jumpstart.com.utils.JwtUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

@RestController 
@RequestMapping("/api/v1/user")
public class DeliveryController {
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	
	
	// Cash On Delivery
	@PostMapping("/products/{ddid}/{pid}/{qty}")
	public ResponseEntity<DeliveryDto> orderProduct( @PathVariable Long ddid,@PathVariable Long pid,@PathVariable int qty,
			HttpServletRequest request) {

		DeliveryDto orderedMeal = this.deliveryService.orderProduct(ddid, pid, qty, jwtUtils.getJWTFromRequest(request));

		return new ResponseEntity<DeliveryDto>(orderedMeal, HttpStatus.CREATED);
	}
	
	// user can pay with Stripe
	@PostMapping("/products/stripe/{ddid}/{pid}/{qty}")
	public ResponseEntity<DeliveryDto> charge(@RequestBody DeliveryDto deliveryDto,@PathVariable Long ddid, @PathVariable Long pid,@PathVariable int qty,
    		HttpServletRequest request) throws StripeException {
        Stripe.apiKey = "sk_test_51MfipcKFXCHKp5pgZ0bcX05VXhT5QLNDT75NqUPrk51X2aeE7z2R5N8eYgUJzfP2jBpWeK5a5P2ds6HeC44mXjBx00TXSdsD31";
        Map<String, Object> params = new HashMap<>();
        Product product = this.productRepository.findById(pid)
				.orElseThrow(() -> new ResourceNotFoundException("product", "product id", pid.toString()));
       if(deliveryDto.getAmount() < product.getPrice()*qty) {
    	   return new ResponseEntity<DeliveryDto>(HttpStatus.BAD_REQUEST);
       }
       int amount = (int)(deliveryDto.getAmount() * 100);
        params.put("amount", amount);
        params.put("currency", "usd");
        params.put("payment_method_types", Collections.singletonList("card"));
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        deliveryDto.setPaymentIntentId(paymentIntent.getId());
        DeliveryDto chargeRequest = deliveryService.orderByStripe(deliveryDto, ddid, pid, qty,  jwtUtils.getJWTFromRequest(request));
	    return new ResponseEntity<DeliveryDto>(chargeRequest, HttpStatus.CREATED);
    }
	
	// get all user orders
	@GetMapping("/products/orders")
	public ResponseEntity<List<DeliveryDto>> allDetailsByUser(HttpServletRequest request) {
		List<DeliveryDto> availableMeals = this.deliveryService.getOrdersByUser(jwtUtils.getJWTFromRequest(request));
		if (availableMeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(availableMeals);
	}
	

	// get specific store by id
	@GetMapping("/delivery/{deliveryId}")
	public ResponseEntity<DeliveryDto> getStore(@PathVariable Long deliveryId) {
		DeliveryDto deliveryDto = this.deliveryService.getDelivery(deliveryId);
		return new ResponseEntity<DeliveryDto>(deliveryDto, HttpStatus.OK);

	}
	
	
	


	// user all deliveries
	@GetMapping("/products")
	public ResponseEntity<List<DeliveryDto>> getOrderedProducts(HttpServletRequest request) {
		List<DeliveryDto> orderedProduct = this.deliveryService.userOrders(jwtUtils.getJWTFromRequest(request));

		if (orderedProduct.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		return ResponseEntity.status(HttpStatus.OK).body(orderedProduct);
	}
	// particular delivery
	@GetMapping("/orders/{deliveryId}")
	public ResponseEntity<DeliveryDto> getParticularOrderedMeal(@PathVariable Long deliveryId) {

		DeliveryDto orderedMeal = this.deliveryService.order(deliveryId);

		return ResponseEntity.status(HttpStatus.OK).body(orderedMeal);
	}
	
	@GetMapping("/orders/products")
	public ResponseEntity<List<DeliveryDto>> getAlldeliveryForUserStores(HttpServletRequest request) {
	    List<DeliveryDto> products = deliveryService.getAllStoreDetailsOrder(jwtUtils.getJWTFromRequest(request));
	    return ResponseEntity.ok().body(products);
	}
	
}
