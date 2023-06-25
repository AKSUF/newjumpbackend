package com.jumpstart.com.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jumpstart.com.payloads.AccountDto;
import com.jumpstart.com.payloads.StoreDto;

public interface StoreService {
	public StoreDto addStore(StoreDto storeDto,String token);
	void uploadImage(MultipartFile multipartFile, Long sid) throws IOException;
	StoreDto updateStore(StoreDto storeDto, String token);
	public void deleteStore(Long sid);
	public StoreDto getStore(Long sid);
	public List<StoreDto> getAllStoreDetails();
//	List<Long> getAccountIdsByStoreId(Long storeId);
	List<AccountDto> getAccountsByStoreId(Long storeId);
}
