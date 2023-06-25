package com.jumpstart.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jumpstart.com.entities.Store;

public interface StoreRepository extends JpaRepository<Store, Long>{
	  @Query("SELECT COUNT(s) FROM Store s")
	    Long countStores();
}
