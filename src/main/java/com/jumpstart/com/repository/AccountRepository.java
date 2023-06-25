package com.jumpstart.com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jumpstart.com.entities.Account;

public interface AccountRepository extends JpaRepository <Account, Long> {
	public Optional<Account> findByEmail(String email);
}
