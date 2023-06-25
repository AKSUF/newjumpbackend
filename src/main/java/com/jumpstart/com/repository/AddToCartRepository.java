package com.jumpstart.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jumpstart.com.entities.AddtoCart;
import com.jumpstart.com.entities.Product;
import com.jumpstart.com.entities.User;

public interface AddToCartRepository extends JpaRepository<AddtoCart,Long> {
	List<AddtoCart> findByUser(User user);
	List<AddtoCart> findByProduct(Product product);
	AddtoCart findByUserAndProduct(User user, Product product);
}
