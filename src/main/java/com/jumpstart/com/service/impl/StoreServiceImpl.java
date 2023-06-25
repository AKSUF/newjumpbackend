package com.jumpstart.com.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Store;
import com.jumpstart.com.entities.UserStore;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.AccountDto;
import com.jumpstart.com.payloads.StoreDto;
import com.jumpstart.com.payloads.UserDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.StoreRepository;
import com.jumpstart.com.repository.UserStoreRepository;
import com.jumpstart.com.service.FileService;
import com.jumpstart.com.service.StoreService;
import com.jumpstart.com.utils.JwtUtils;

@Service
public class StoreServiceImpl implements StoreService {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	StoreRepository storeRepository;
	
	@Autowired
	UserStoreRepository userStoreRepository;
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private FileService fileService;
	
	@Value("${project.store}")
	private String path;

	// shopkeeper can add product 
	@Override
	public StoreDto addStore(StoreDto storeDto,String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		Store store = this.modelMapper.map(storeDto,Store.class);
		store.setUser(account.getUser());
		Store saveStoreDetails = this.storeRepository.save(store);
		return this.modelMapper.map(saveStoreDetails, StoreDto.class);
	}

	// shopkeeper can edit product 
	@Override
	public StoreDto updateStore(StoreDto storeDto,String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		Store store = this.modelMapper.map(storeDto, Store.class);
//		store.setStore_name(storeDto.getStore_name());
//		store.setStore_address(storeDto.getStore_address());
//		store.setStore_desc(storeDto.getStore_desc());
		store.setUser(account.getUser());
		Store updateStore = this.storeRepository.save(store);
		return this.modelMapper.map(updateStore, StoreDto.class);
	
	}

	// shopkeeper can delete his/her product
	@Override
	public void deleteStore(Long sid) {
		Store store = this.storeRepository.findById(sid).orElseThrow(()-> 
		new ResourceNotFoundException("Store", "Store id", sid.toString()));
		this.storeRepository.delete(store);
		
	}

	@Override
	public StoreDto getStore(Long sid) {
		Store store = this.storeRepository.findById(sid).orElseThrow(()->
		new ResourceNotFoundException("Store", "Store id", sid.toString()));
		return this.modelMapper.map(store, StoreDto.class);
	}

	@Override
	public List<StoreDto> getAllStoreDetails() {
		List<Store> store = this.storeRepository.findAll();
		List<StoreDto> storeDto=store.stream().map((s)->
		this.modelMapper.map(s, StoreDto.class)).collect(Collectors.toList());
		return storeDto;
	}

	
	@Override
	public void uploadImage(MultipartFile multipartFile, Long sid) throws IOException {
		Store store = this.storeRepository.findById(sid)
				.orElseThrow(() -> new ResourceNotFoundException("Store", "Store id", sid.toString()));

		// deleting old image
		if (store.getStore_image() != null) {
			this.fileService.deleteFile(store.getStore_image());
		}

		// getting new file name
		String uploadedImage = fileService.uploadImage(path,multipartFile);

		// setting image name
		store.setStore_image(uploadedImage);

		// updating user
		this.storeRepository.save(store);
		
	}
	
	public List<AccountDto> getAccountsByStoreId(Long storeId) {
	    Optional<Store> storeOptional = storeRepository.findById(storeId);
	    if (storeOptional.isPresent()) {
	        List<UserStore> userStores = userStoreRepository.findByStore(storeOptional.get());
	        List<AccountDto> accountDtos = userStores.stream()
	                .map(userStore -> userStore.getAccount())
	                .map(account -> {
	                    AccountDto accountDto = new AccountDto();
	                    accountDto.setAccount_id(account.getAccount_id());
	                    accountDto.setEmail(account.getEmail());
	                    accountDto.setAddedDate(account.getAddedDate());
	                    accountDto.setProvider(account.getProvider());
	                    if (account.getUser() != null) {
	                        UserDto userDto = new UserDto();
	                        userDto.setUser_id(account.getUser().getUser_id());
	                        userDto.setName(account.getUser().getName());
	                        userDto.setPhone_number(account.getUser().getPhone_number());
	                        userDto.setProfile_image(account.getUser().getProfile_image());
	                        userDto.setDistrict(account.getUser().getDistrict());
	                        userDto.setAddress(account.getUser().getAddress());
	                        accountDto.setUser(userDto);
	                    }
	                    return accountDto;
	                })
	                .collect(Collectors.toList());
	        return accountDtos;
	    }
	    return null;
	}

	
}
