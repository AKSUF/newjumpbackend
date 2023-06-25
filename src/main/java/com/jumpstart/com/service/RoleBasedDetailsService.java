package com.jumpstart.com.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.payloads.RoleBasedDetailsDto;

public interface RoleBasedDetailsService {
	void uploadImage(MultipartFile multipartFile, Long srid) throws IOException;

	RoleBasedDetailsDto createUserProfile(RoleBasedDetailsDto roleBasedDetailsDto, Long srid);

	RoleBasedDetailsDto createShopkeerperProfile(RoleBasedDetailsDto roleBasedDetailsDto, Long srid, Long Sid);

	RoleBasedDetailsDto createToleProfile(RoleBasedDetailsDto roleBasedDetailsDto, String token);

	RoleBasedDetailsDto getUserRoleDetails(String token);

	List<RoleBasedDetailsDto> getAllUserRequest();

	void deleteRoleBasedDetailsDto(String token);
}
