package com.jumpstart.com.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jumpstart.com.payloads.AccountDto;
import com.jumpstart.com.service.AccountService;
import com.jumpstart.com.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
	@Autowired
	private AccountService accountService;

	@Autowired
	private JwtUtils jwtUtils;

	// get all registered accounts
	@GetMapping("/")
	public ResponseEntity<List<AccountDto>> allAccounts() {
		List<AccountDto> accountDtos = this.accountService.getAllAccountDto();
		if (accountDtos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(accountDtos);
	}

	// get particular account
	@GetMapping("/particular-account")
	public ResponseEntity<AccountDto> getAccountInfo(HttpServletRequest request) {

		return ResponseEntity.status(HttpStatus.OK)
				.body(this.accountService.getAccountDto(jwtUtils.getJWTFromRequest(request)));
	}
}
