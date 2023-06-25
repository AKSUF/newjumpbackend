package com.jumpstart.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jumpstart.com.entities.Product;
import com.jumpstart.com.entities.Store;
import com.jumpstart.com.entities.User;

public interface ProductRepository extends JpaRepository<Product, Long> {
	@Query(value = "SELECT p FROM Product p WHERE p.productName LIKE '%' || :keyword || '%'"
			+ " OR p.category LIKE '%' || :keyword || '%'"
			+ " OR p.price LIKE '%' || :keyword || '%'")
	public List<Product> search(@Param("keyword") String keyword);
	List<Product> findByApprovedTrue();
	 List<Product> findByApprovedFalse();
	public List<Product> findByStore(Store store);
	public List<Product> findAllByUser(User user);
}
