package com.jumpstart.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jumpstart.com.entities.DeliveryDetails;
import com.jumpstart.com.entities.User;

public interface DeliveryDetailsRepository extends JpaRepository<DeliveryDetails, Long> {
	List<DeliveryDetails> findByUser(User user);
}
