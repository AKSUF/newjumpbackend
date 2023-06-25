package com.jumpstart.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jumpstart.com.entities.Delivery;
import com.jumpstart.com.entities.RiderDelivery;

public interface RiderDeliveryRepository  extends JpaRepository<RiderDelivery, Long> {

	void deleteByDelivery(Delivery delivery);

}
