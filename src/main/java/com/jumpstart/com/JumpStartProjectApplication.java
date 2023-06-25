package com.jumpstart.com;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jumpstart.com.config.AppConstants;
import com.jumpstart.com.entities.Role;
import com.jumpstart.com.repository.RoleRepository;

@SpringBootApplication
public class JumpStartProjectApplication   implements CommandLineRunner {

	@Autowired
	private RoleRepository roleRepository;
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(JumpStartProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		try {

			// admin role
			Role role_admin = new Role();
			role_admin.setRole_id(AppConstants.ROLE_ADMIN.longValue());
			role_admin.setRole_name("ROLE_ADMIN");

			// user role
			Role role_user = new Role();
			role_user.setRole_id(AppConstants.ROLE_USER.longValue());
			role_user.setRole_name("ROLE_USER");

			// employee role
			Role role_employee = new Role();
			role_employee.setRole_id(AppConstants.ROLE_EMPLOYEE.longValue());
			role_employee.setRole_name("ROLE_EMPLOYEE");

			// rider role
			Role role_rider = new Role();
			role_rider.setRole_id(AppConstants.ROLE_RIDER.longValue());
			role_rider.setRole_name("ROLE_RIDER");

			// producer role
			Role role_producer = new Role();
			role_producer.setRole_id(AppConstants.ROLE_PRODUCER.longValue());
			role_producer.setRole_name("ROLE_PRODUCER");

			// sales role
			Role role_store = new Role();
			role_store.setRole_id(AppConstants.ROLE_STORE.longValue());
			role_store.setRole_name("ROLE_STORE");
			
			// courier role
			Role role_shipping_courier = new Role();
			role_shipping_courier.setRole_id(AppConstants.ROLE_SHIPPING_COURIER.longValue());
			role_shipping_courier.setRole_name("ROLE_SHIPPING_COURIER");

			List<Role> roles = roleRepository.saveAll(Arrays.asList(role_admin, role_user, role_employee, role_rider, role_producer,
					role_store,role_shipping_courier));

			this.roleRepository.saveAll(roles);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
}
