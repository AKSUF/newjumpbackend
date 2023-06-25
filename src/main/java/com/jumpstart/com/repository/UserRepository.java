package com.jumpstart.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jumpstart.com.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.stores")
    List<User> findAllWithStores();
}
