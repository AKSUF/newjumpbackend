package com.jumpstart.com.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumpstart.com.entities.Role;
import com.jumpstart.com.payloads.RoleDto;
import com.jumpstart.com.repository.RoleRepository;
import com.jumpstart.com.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
	@Autowired 
	RoleRepository roleRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<RoleDto> getRoles() {
		List<Role> role = this.roleRepository.findAll();
		List<RoleDto> roleDto=role.stream().map((s)->
		this.modelMapper.map(s, RoleDto.class)).collect(Collectors.toList());
		return roleDto;
	}
	
}
