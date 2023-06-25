package com.jumpstart.com.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.DeliveryDetails;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.DeliveryDetailsDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.DeliveryDetailsRepository;
import com.jumpstart.com.service.DeliveryDetailsService;
import com.jumpstart.com.utils.JwtUtils;

@Service
public class DeliveryDetailsServiceImpl implements DeliveryDetailsService{

	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private DeliveryDetailsRepository deliveryDetailsRepository;
	// User will give his delivery details where the rider/deliveryman will deliver the order
	@Override
	public DeliveryDetailsDto addDeliverDetails(DeliveryDetailsDto deliveryDetailsDto, String token) {
				String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		DeliveryDetails deliveryDetails = this.modelMapper.map(deliveryDetailsDto, DeliveryDetails.class);
		deliveryDetails.setUser(account.getUser());

		
		DeliveryDetails newOrder = this.deliveryDetailsRepository.save(deliveryDetails);

		return this.modelMapper.map(newOrder, DeliveryDetailsDto.class);
	}
	
	
	// User can update his/her delivery-details
	@Override
	public DeliveryDetailsDto updateDeliveryDetails(DeliveryDetailsDto deliveryDetailsDto, String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		this.deliveryDetailsRepository.findById(deliveryDetailsDto.getDelivery_details_id())
				.orElseThrow(() -> new ResourceNotFoundException("meal", "meal id", deliveryDetailsDto.getDelivery_details_id().toString()));

		DeliveryDetails updateDeliveryDetails = this.modelMapper.map(deliveryDetailsDto, DeliveryDetails.class);
		updateDeliveryDetails.setUser(account.getUser());

		this.deliveryDetailsRepository.save(updateDeliveryDetails);

		return this.modelMapper.map(updateDeliveryDetails, DeliveryDetailsDto.class);
	}
	
	// get all user delivery-details
	@Override
	public List<DeliveryDetailsDto> getDeliveryDetailsByUser(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		List<DeliveryDetails> deliveryDetails = this.deliveryDetailsRepository.findByUser(account.getUser());
		

		List<DeliveryDetailsDto> deliveryDetails1 = deliveryDetails.stream().map(order -> this.modelMapper.map(order, DeliveryDetailsDto.class))
				.collect(Collectors.toList());

		return deliveryDetails1;
	}
	

	// Get user specific delivery details with id
	@Override
	public DeliveryDetailsDto getDeliveryId(Long ddid) {
		DeliveryDetails deliveryDetails = this.deliveryDetailsRepository.findById(ddid)
				.orElseThrow(() -> new ResourceNotFoundException("Delivery Details", "Delivery Details id", ddid.toString()));

		return this.modelMapper.map(deliveryDetails, DeliveryDetailsDto.class);
	}
	

	// Delete user specific delivery details with id
	@Override
	public void deleteDeliveryDetails(Long ddid) {
		DeliveryDetails deliveryDetails = this.deliveryDetailsRepository.findById(ddid).orElseThrow(()-> 
		new ResourceNotFoundException("Delivery Details", "Delivery Details id", ddid.toString()));
		this.deliveryDetailsRepository.delete(deliveryDetails);
		
	}
}
