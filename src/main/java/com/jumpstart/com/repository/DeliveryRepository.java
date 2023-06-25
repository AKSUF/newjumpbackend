package com.jumpstart.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jumpstart.com.entities.Delivery;
import com.jumpstart.com.entities.Store;
import com.jumpstart.com.entities.User;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
List<Delivery> findByUser(User user);
	
	List<Delivery> findByStatus(String delivery_status);

	void delete(Delivery deli);
	    List<Delivery> findByProductStoreAndUser(Store  store, User user);

		List<Delivery> findByProductStore(Store store);

}
