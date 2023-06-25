package com.jumpstart.com.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.User;
import com.jumpstart.com.payloads.UserDto;

public interface UserService {
	// uploading user profile image
		void uploadImage(MultipartFile multipartFile, String token) throws IOException;

		// user create profile
		UserDto createUserProfile(UserDto userDto, String token);

		User getUser(Long userId);

		List<UserDto> getUserProfiles();

		// edit user profile
		UserDto editUserProfile(UserDto userDto, Long userId);
}
