package com.jumpstart.com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Store;
import com.jumpstart.com.entities.UserStore;

public interface UserStoreRepository  extends JpaRepository<UserStore, Long>{
	List<UserStore> findByStore(Store store);

	List<UserStore> findByAccount(Account account);
}
