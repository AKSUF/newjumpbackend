package com.jumpstart.com.service;

import java.util.List;

import com.jumpstart.com.entities.User;
import com.jumpstart.com.payloads.DeliveryDetailsDto;
import com.jumpstart.com.payloads.DeliveryDto;

public interface DeliveryService {

	void deleteDelivery(Long deliveryId);


//	DeliveryDto orderProduct(Long ddid, String token);

	DeliveryDto orderProduct(Long ddid, Long pid, int qty, String token);


	DeliveryDto orderByStripe(DeliveryDto deliveryDto, Long ddid, Long pid, int qty, String token);

	List<DeliveryDto> getOrdersByUser(String token);

	boolean checkDistrictsSame(Long deliveryId, String token);

	List<DeliveryDto> getAllDeliveriesByRider(String token);

//	List<DeliveryDto> getAllDeliveriesByCourier(String token);


	List<DeliveryDto> getAllDeliveriesByEmployee(String token);

	List<DeliveryDto> getAllDeliveriesByCourier();




	void employeeRequestForDeliver(Long deliveryId, Long rid) throws Exception;

//	DeliveryDto orderProductStatus(String token, Long deliveryId, String status);

	DeliveryDto orderProductStatus(User user, Long deliveryId, String status);

	DeliveryDto orderProductStatusForCourier(User user, Long deliveryId, String status);


	DeliveryDto getDelivery(Long deliveryId);


	List<DeliveryDto> userOrders(String token);


	DeliveryDto order(Long deliveryId);


	List<DeliveryDto> getAllStoreDetailsOrder(String token);
}
