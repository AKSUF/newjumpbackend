package com.jumpstart.com.service;

import java.util.List;

import com.jumpstart.com.payloads.DeliveryDetailsDto;

public interface DeliveryDetailsService {

	DeliveryDetailsDto addDeliverDetails(DeliveryDetailsDto deliveryDetailsDto, String token);

	DeliveryDetailsDto updateDeliveryDetails(DeliveryDetailsDto deliveryDetailsDto, String token);

	List<DeliveryDetailsDto> getDeliveryDetailsByUser(String token);

	DeliveryDetailsDto getDeliveryId(Long ddid);

	void deleteDeliveryDetails(Long ddid);
}
