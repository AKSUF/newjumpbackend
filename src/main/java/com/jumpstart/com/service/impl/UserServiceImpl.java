package com.jumpstart.com.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.UserDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.UserRepository;
import com.jumpstart.com.service.FileService;
import com.jumpstart.com.service.UserService;
import com.jumpstart.com.utils.JwtUtils;



@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private FileService fileService;

	@Autowired
	private JwtUtils jwtUtils;
	@Value("${project.image}")
	private String path;

	public void uploadImage(MultipartFile multipartFile, String token) throws IOException {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		User user = account.getUser();

		// deleting old image
		if (user.getProfile_image() != null) {
			this.fileService.deleteFile(user.getProfile_image());
		}

		// getting new file name
		String uploadedImage = fileService.uploadImage(path,multipartFile);

		// setting image name
		user.setProfile_image(uploadedImage);

		// updating user
		this.userRepository.save(user);
	}


	@Override
	public UserDto createUserProfile(UserDto userDto, String token) {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		User user = this.modelMapper.map(userDto, User.class);
		user.setAccount(account);
		user.setProfile_image("default.png");
		user = userRepository.save(user);
		return this.modelMapper.map(user, UserDto.class);
	}

	@Override
	public User getUser(Long userId) {
		Optional<User> user = userRepository.findById(userId);

		if(!user.isPresent()) {
			return null;
		}
			return user.get();
	}

	@Override
	public List<UserDto> getUserProfiles() {
		
		List<User> users = userRepository.findAll();
		List<UserDto> userDto = new ArrayList<UserDto>();
		users.stream().forEach((user) -> {
			userDto.add(this.modelMapper.map(user, UserDto.class));
		});
		return userDto;
	}
//	@Override
//	public List<UserDto> getUserProfiles() {
//	    List<User> users = userRepository.findAllWithStores();
//	    List<UserDto> userDtoList = new ArrayList<>();
//	    for (User user : users) {
//	        UserDto userDto = modelMapper.map(user, UserDto.class);
//	        Set<StoreDto> storeDtoSet = userDto.getStores();
//	        for (Store store : user.getStore()) {
//	            storeDtoSet.add(modelMapper.map(store, StoreDto.class));
//	        }
//	        userDtoList.add(userDto);
//	    }
//	    return userDtoList;
//	}


	@Override
	public UserDto editUserProfile(UserDto userDto, Long userId) {
		Optional<User> user = userRepository.findById(userId);
		
		User editUser =this.modelMapper.map(userDto, User.class);
		User realUser = user.get();
		editUser.setAccount(realUser.getAccount());
		editUser.setProfile_image(realUser.getProfile_image());
		editUser.setUser_id(realUser.getUser_id());
		editUser = userRepository.save(editUser);
		userDto = this.modelMapper.map(editUser, UserDto.class);
		return userDto;
	}
	
	
}
