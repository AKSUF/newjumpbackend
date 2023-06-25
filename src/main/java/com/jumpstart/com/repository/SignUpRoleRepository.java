package com.jumpstart.com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jumpstart.com.entities.SignUpRole;

public interface SignUpRoleRepository  extends JpaRepository<SignUpRole, Long> {
	public Optional<SignUpRole> findByEmail(String email);

	public SignUpRole findByToken(String token);

	public boolean existsByEmail(String email);
}
