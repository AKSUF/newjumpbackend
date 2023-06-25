package com.jumpstart.com.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Delivery;
import com.jumpstart.com.entities.DeliveryDetails;
import com.jumpstart.com.entities.Product;
import com.jumpstart.com.entities.RiderDelivery;
import com.jumpstart.com.entities.Store;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.entities.UserStore;
import com.jumpstart.com.exception.InsufficientQuantityExceptio;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.DeliveryDetailsDto;
import com.jumpstart.com.payloads.DeliveryDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.DeliveryDetailsRepository;
import com.jumpstart.com.repository.DeliveryRepository;
import com.jumpstart.com.repository.ProductRepository;
import com.jumpstart.com.repository.RiderDeliveryRepository;
import com.jumpstart.com.repository.UserRepository;
import com.jumpstart.com.repository.UserStoreRepository;
import com.jumpstart.com.service.DeliveryService;
import com.jumpstart.com.status.PaymentType;
import com.jumpstart.com.utils.JwtUtils;

@Service
public class DeliverServiceImpl implements DeliveryService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private DeliveryDetailsRepository deliveryDetailsRepository;

	@Autowired
	private DeliveryRepository deliveryRepository;
	
	@Autowired
	private RiderDeliveryRepository riderDeliveryRepo;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private UserStoreRepository userStoreRepository;
	@Autowired
	private RiderDeliveryRepository riderDeliveryRepository;


	@Autowired
	private JwtUtils jwtUtils;
	
	

	@Autowired
	private ModelMapper modelMapper;



	@Override
	public DeliveryDto orderProduct(Long ddid, Long pid, int qty, String token) {
	    DeliveryDetails deliveryDetails = this.deliveryDetailsRepository.findById(ddid)
	            .orElseThrow(() -> new ResourceNotFoundException("DeliveryDetails", "DeliveryDetails id", ddid.toString()));
	    Product product = this.productRepository.findById(pid)
	            .orElseThrow(() -> new ResourceNotFoundException("product", "product id", pid.toString()));

	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

	    if (product.getAvailableQuantity() < qty) {
	        throw new InsufficientQuantityExceptio("Insufficient quantity for product with id " + pid);
	    }

	    Delivery delivery = new Delivery();

	    delivery.setDelivery_number((int) (Math.floor(Math.random() * (99999999 - 00000000)) + 00000000));
	    delivery.setStatus("PENDING");
	    delivery.setDeliveryDetails(deliveryDetails);
	    delivery.setPaidOrPayable("Payable");
	    delivery.setUser(account.getUser());
	    delivery.setProduct(product);
	    delivery.setQty(qty);
	    delivery.setAmount((product.getPrice() * qty)+product.getCharge());
	    delivery.setPayment_type(PaymentType.CashOnDelivery.name());
	    delivery.setOrderDate(new Date());
	 // set standard delivery date to 7 days from now
	    Date standardDeliveryDate = new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
	    delivery.setStanderedDeliveryDate(standardDeliveryDate);

	    // update availableQuantity of product
	    product.setAvailableQuantity(product.getAvailableQuantity() - qty);
	    if(product.getAvailableQuantity()==0) {
	    	product.setStatus("Out Of Stock");
	    }

	    Delivery newOrder = this.deliveryRepository.save(delivery);

	    return this.modelMapper.map(newOrder, DeliveryDto.class);
	}
	
	
	@Override
	public List<DeliveryDto> getOrdersByUser(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		List<Delivery> deliveryDetails = this.deliveryRepository.findByUser(account.getUser());
		

		List<DeliveryDto> deliveryDetails1 = deliveryDetails.stream().map(order -> this.modelMapper.map(order, DeliveryDto.class))
				.collect(Collectors.toList());

		return deliveryDetails1;
	}
	
	
	
	
	

	@Override
    public DeliveryDto orderByStripe(DeliveryDto deliveryDto, Long ddid,Long pid, int qty, String token) {
        // Retrieve the delivery associated with the charge request
        DeliveryDetails deliveryDetails = deliveryDetailsRepository.findById(ddid)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
        Product product = this.productRepository.findById(pid)
				.orElseThrow(() -> new ResourceNotFoundException("product", "product id", pid.toString()));
        String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));


        // Map ChargeRequestDto to ChargeRequest entity
        Delivery delivery = modelMapper.map(deliveryDto, Delivery.class);

        // Set the delivery and user associated with the charge request
        delivery.setDeliveryDetails(deliveryDetails);
        delivery.setUser(account.getUser());
        delivery.setDelivery_number((int) (Math.floor(Math.random() * (99999999 - 00000000)) + 00000000));
		delivery.setStatus("PENDING");
		delivery.setPaidOrPayable("Paid");
		delivery.setOrderDate(new Date());

		delivery.setProduct(product);
		delivery.setQty(qty);
		delivery.setAmount((product.getPrice() * qty)+product.getCharge());
		delivery.setPayment_type(PaymentType.Stripe.name());
		// set standard delivery date to 7 days from now
	    Date standardDeliveryDate = new Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
	    delivery.setStanderedDeliveryDate(standardDeliveryDate);
		 product.setAvailableQuantity((product.getAvailableQuantity() - qty));
		 if(product.getAvailableQuantity()==0) {
		    	product.setStatus("Out Of Stock");
		    }


        // Save the charge request to the database
        Delivery savedChargeRequest = deliveryRepository.save(delivery);

        // Map ChargeRequest entity to ChargeRequestDto
        DeliveryDto savedChargeRequestDto = modelMapper.map(savedChargeRequest, DeliveryDto.class);
		return savedChargeRequestDto;

    }
	
	
	
	
	
	@Override
    public boolean checkDistrictsSame(Long deliveryId, String token) {
        String email = jwtUtils.getUserNameFromToken(token);
        Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
				
		
		Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new RuntimeException("Delivery not found"));
        
        String productDistrict = delivery.getProduct().getShippingAddress();
        String userDistrict = account.getUser().getDistrict();
        String deliveryDetailsDistrict = delivery.getDeliveryDetails().getDistrict();
        
        return productDistrict.equals(userDistrict) && userDistrict.equals(deliveryDetailsDistrict);
    }
	
	
	  // If the district is the same, the order will come to the rider 
	@Override
	public List<DeliveryDto> getAllDeliveriesByRider(String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

	    List<Delivery> deliveries = deliveryRepository.findAll();
	    String userDistrict = account.getUser().getDistrict();

	    List<DeliveryDto> deliveryDtos = deliveries.stream()
	            .filter(delivery -> delivery.getProduct().getShippingAddress().equals(userDistrict)
	                    && delivery.getDeliveryDetails().getDistrict().equals(userDistrict))
	            .map(delivery -> {
	                DeliveryDto dto = modelMapper.map(delivery, DeliveryDto.class);
	                dto.getProduct().setShippingAddress(delivery.getProduct().getShippingAddress());
	                dto.getUser().setDistrict(userDistrict);
	                dto.getDeliveryDetails().setDistrict(delivery.getDeliveryDetails().getDistrict());
	                return dto;
	            })
	            .collect(Collectors.toList());

	    return deliveryDtos;
	}
	

	@Override
	public DeliveryDto order(Long deliveryId) {
		Delivery delivery = this.deliveryRepository.findById(deliveryId)
				.orElseThrow(() -> new ResourceNotFoundException("delivery", "delivery id", deliveryId.toString()));

		return this.modelMapper.map(delivery, DeliveryDto.class);
	}
	
	
	//If the district is different the orders will go to the courier
	@Override
	public List<DeliveryDto> getAllDeliveriesByCourier() {
	    List<Delivery> deliveries = deliveryRepository.findAll();

	    List<DeliveryDto> notMatchedDeliveryDtos = deliveries.stream()
	        .filter(delivery -> !delivery.getProduct().getShippingAddress().equals(delivery.getDeliveryDetails().getDistrict()))
	        .map(delivery -> {
	            DeliveryDto dto = modelMapper.map(delivery, DeliveryDto.class);
	            dto.getProduct().setShippingAddress(delivery.getProduct().getShippingAddress());
	            dto.getDeliveryDetails().setDistrict(delivery.getDeliveryDetails().getDistrict());
	            return dto;
	        })
	        .collect(Collectors.toList());

	    return notMatchedDeliveryDtos;
	}


	 // If the user district and product shipping address is the same, the order will come to the rider 
	@Override
	public List<DeliveryDto> getAllDeliveriesByEmployee(String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

	    List<Delivery> deliveries = deliveryRepository.findAll();

	    List<DeliveryDto> empDeliveryDtos = deliveries.stream()
	            .filter(delivery -> delivery.getProduct().getShippingAddress().equals(delivery.getDeliveryDetails().getDistrict()))
	            .map(delivery -> {
	                DeliveryDto dto = modelMapper.map(delivery, DeliveryDto.class);
	                dto.getProduct().setShippingAddress(delivery.getProduct().getShippingAddress());
	                dto.getDeliveryDetails().setDistrict(delivery.getDeliveryDetails().getDistrict());
	                return dto;
	            })
	            .collect(Collectors.toList());
		return empDeliveryDtos;

	}
	
	
	// Rider can change his delivery status
	@Override
	public DeliveryDto orderProductStatus(User user, Long deliveryId, String status) {
	    Delivery delivery = this.deliveryRepository.findById(deliveryId)
	            .orElseThrow(() -> new ResourceNotFoundException("delivery", "delivery id", deliveryId.toString()));

	    delivery.setStatus(status);
	    System.out.println(status);
	    System.out.println(status.equalsIgnoreCase("ACCEPT_FOR_DELIVER") + "/////////////////////////////////////////");
	    if(status.equalsIgnoreCase("ACCEPT_FOR_DELIVER")) {
	        System.out.println("+++++++++++++++"+status);
	        RiderDelivery rideDelivery = new RiderDelivery();
	        rideDelivery.setRider(user);
	        rideDelivery.setDelivery(delivery);
	        this.riderDeliveryRepo.save(rideDelivery);
	        delivery.setRiderDelivery(rideDelivery); // set the riderDelivery field of the delivery object
	    }
	    if(status.equalsIgnoreCase("DELIVERED")) {
	        System.out.println("+++++++++++++++"+status);
	        delivery.setPaidOrPayable("Paid");
	 // set the riderDelivery field of the delivery object
	    }

	    Delivery changedStatus = this.deliveryRepository.save(delivery);

	    return this.modelMapper.map(changedStatus, DeliveryDto.class);
	}
	
	// Courier can change his delivery status
	@Override
	public DeliveryDto orderProductStatusForCourier(User user, Long deliveryId, String status) {
	    Delivery delivery = this.deliveryRepository.findById(deliveryId)
	            .orElseThrow(() -> new ResourceNotFoundException("delivery", "delivery id", deliveryId.toString()));

	    delivery.setStatus(status);
	    System.out.println(status);
	    System.out.println(status.equalsIgnoreCase("OUT_FOR_DELIVER") + "/////////////////////////////////////////");
	    if(status.equalsIgnoreCase("OUT_FOR_DELIVER")) {
	        System.out.println("+++++++++++++++"+status);
	        RiderDelivery rideDelivery = new RiderDelivery();
	        rideDelivery.setRider(user);
	        rideDelivery.setDelivery(delivery);
	        this.riderDeliveryRepo.save(rideDelivery);
	        delivery.setRiderDelivery(rideDelivery); // set the riderDelivery field of the delivery object
	    }
	    if(status.equalsIgnoreCase("DELIVERED")) {
	        System.out.println("+++++++++++++++"+status);
	        delivery.setPaidOrPayable("Paid");
	 // set the riderDelivery field of the delivery object
	    }

	    Delivery changedStatus = this.deliveryRepository.save(delivery);

	    return this.modelMapper.map(changedStatus, DeliveryDto.class);
	}
	
	
	@Override
	public List<DeliveryDto> getAllStoreDetailsOrder(String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    User user = account.getUser();
	    List<UserStore> userStores = userStoreRepository.findByAccount(account);
	    Set<Store> stores = userStores.stream().map(UserStore::getStore).collect(Collectors.toSet());

	    List<DeliveryDto> productDtos = new ArrayList<>();
	    for (Store store : stores) {
	        List<Delivery> products = deliveryRepository.findByProductStore(store);
	        List<DeliveryDto> storeProductDtos = products.stream()
	                .map(product -> modelMapper.map(product, DeliveryDto.class))
	                .collect(Collectors.toList());
	        productDtos.addAll(storeProductDtos);
	    }

	    return productDtos;
	}


	
	
//
//	@Override
//	public DeliveryDto orderProductStatus(String token, Long deliveryId, String status) {
//		Delivery delivery = this.deliveryRepository.findById(deliveryId)
//				.orElseThrow(() -> new ResourceNotFoundException("delivery", "delivery id", deliveryId.toString()));
//		 String email = jwtUtils.getUserNameFromToken(token);
//		    Account account = accountRepo.findByEmail(email)
//		            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
//		delivery.setStatus(status);
//		System.out.println(status);
//		System.out.println("_______________"+account.getUser());
//		if(status.equalsIgnoreCase("PENDING")) {
//			System.out.println(status);
//			RiderDelivery rideDelivery = new RiderDelivery();
//			rideDelivery.setRider(account.getUser());
//			rideDelivery.setDelivery(delivery);
//			System.out.println(rideDelivery);
//			this.riderDeliveryRepo.save(rideDelivery);
//		}
//
//		Delivery changedStatus = this.deliveryRepository.save(delivery);
//
//		return this.modelMapper.map(changedStatus, DeliveryDto.class);
//	}
	
//	@Override
//	public DeliveryDto employeeRequestForDeliver(Long deliveryId, String status) {
//		Delivery delivery = this.deliveryRepository.findById(deliveryId)
//				.orElseThrow(() -> new ResourceNotFoundException("delivery", "delivery id", deliveryId.toString()));
//
//		delivery.setStatus(status);
//		System.out.println(status);
//		if(status.equalsIgnoreCase("PENDING")) {
//			System.out.println(status);
//			RiderDelivery rideDelivery = new RiderDelivery();
//			rideDelivery.setRider(user);
//			rideDelivery.setDelivery(delivery);
//			this.riderDeliveryRepo.save(rideDelivery);
//		}
//
//		Delivery changedStatus = this.deliveryRepository.save(delivery);
//
//		return this.modelMapper.map(changedStatus, DeliveryDto.class);
//	}
	@Override
	public void employeeRequestForDeliver(Long deliveryId, Long rid) throws Exception {
	    Delivery delivery = this.deliveryRepository.findById(deliveryId)
	    		
	        .orElseThrow(() -> new ResourceNotFoundException("delivery", "delivery id", deliveryId.toString()));
//	    System.out.println("++++++++++++++"+deliveryId);
	    User user = this.userRepository.findById(rid)
		        .orElseThrow(() -> new ResourceNotFoundException("Rider", "Rider id", rid.toString()));
	    RiderDelivery riderDelivery = new RiderDelivery();
	    riderDelivery.setRider(user);
	    riderDelivery.setDelivery(delivery);
//delivery.getRiderDelivery().setDelivery(delivery);;
//	    delivery.getRiderDelivery().setRider(riderDelivery);
//
	    delivery.setDeliveryRequest("REQUESTED");

	    this.riderDeliveryRepository.save(riderDelivery);
	    this.deliveryRepository.save(delivery);
	}
	
	

	@Override
	public void deleteDelivery(Long deliveryId) {
		Delivery delivery = deliveryRepository.findById(deliveryId).get();
		this.riderDeliveryRepo.deleteByDelivery(delivery);
		this.deliveryRepository.delete(delivery);
		return;
	}


	@Override
	public DeliveryDto getDelivery(Long deliveryId) {
Delivery delivery = this.deliveryRepository.findById(deliveryId).orElseThrow(()->
		new ResourceNotFoundException("Store", "Store id", deliveryId.toString()));
		return this.modelMapper.map(delivery, DeliveryDto.class);
	}	
	

	@Override
	public List<DeliveryDto> userOrders(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		List<Delivery> userOrders = this.deliveryRepository.findByUser(account.getUser());

		List<DeliveryDto> ordersDtos = userOrders.stream().map(order -> this.modelMapper.map(order, DeliveryDto.class))
				.collect(Collectors.toList());

		return ordersDtos;
	}

	
}
