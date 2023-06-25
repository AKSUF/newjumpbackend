package com.jumpstart.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jumpstart.com.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	@Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role.role_name = 'ROLE_USER'")
    Long countUserRole();

    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role.role_name = 'ROLE_RIDER'")
    Long countRiderRole();
    
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role.role_name = 'ROLE_EMPLOYEE'")
    Long countEmployeeRole();
}
