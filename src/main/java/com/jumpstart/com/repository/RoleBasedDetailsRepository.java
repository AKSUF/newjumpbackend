package com.jumpstart.com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jumpstart.com.entities.RoleBasedDetails;

public interface RoleBasedDetailsRepository extends JpaRepository<RoleBasedDetails, Long> {
	Optional<RoleBasedDetails> findByJumpStartId(String jumpStartId);

	Optional<RoleBasedDetails> findByToken(String token);
}
