package com.jumpstart.com.service;

import java.util.List;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.payloads.AccountDto;

public interface AccountService {

	// get all accounts
	List<AccountDto> getAllAccountDto();

	// get particular account info
	AccountDto getAccountDto(String token);

	Account getAccount(String token);
	
	void deleteAccount(Long userId);
}
