package com.jumpstart.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jumpstart.com.entities.BuyProduct;
import com.jumpstart.com.entities.User;

public interface BuyProductRepository extends JpaRepository<BuyProduct, Long> {

	List<BuyProduct> findByAcceptedFalse();

	 List<BuyProduct> findByProducerIdAndAcceptedFalse(Long producerId);

	List<BuyProduct> findByAcceptedTrue();

	List<BuyProduct> findByProducerIdAndAcceptedTrue(Long producerId);

	List<BuyProduct> findByUser(User user);
	// custom query method to get all BuyProducts except those with status "not available"
    @Query("SELECT b FROM BuyProduct b WHERE b.status <> 'not available'")
    List<BuyProduct> findAllExceptNotAvailable();
}
