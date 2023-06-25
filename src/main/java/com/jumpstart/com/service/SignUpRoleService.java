package com.jumpstart.com.service;

import java.util.List;

import com.jumpstart.com.entities.SignUpRole;
import com.jumpstart.com.payloads.SignUpRoleDto;

public interface SignUpRoleService {

	void registerUserUsingRole(SignUpRole user, Long rid);

	List<SignUpRoleDto> getAcceptProducts();
}
