package com.jumpstart.com.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.Product;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.entities.UserRole;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.AccountDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.UserRepository;
import com.jumpstart.com.repository.UserRoleRepository;
import com.jumpstart.com.service.AccountService;
import com.jumpstart.com.service.ProductService;
import com.jumpstart.com.utils.JwtUtils;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private ProductService productService;
	
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	



	@Override
	public List<AccountDto> getAllAccountDto() {

		List<Account> allAccounts = this.accountRepo.findAll();

		List<AccountDto> allAccountDtos = allAccounts.stream()
				.map((account) -> this.modelMapper.map(account, AccountDto.class)).collect(Collectors.toList());

		return allAccountDtos;
	}

	@Override
	public AccountDto getAccountDto(String token) {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("account", "credentials", email));

		return this.modelMapper.map(account, AccountDto.class);
	}
	
	@Override
	public Account getAccount(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		return accountRepo.findByEmail(email).get();
	}
	
	@Override
	public void deleteAccount(Long userId) {
		User user = userRepo.findById(userId).get();
		List<Product> products = user.getProducts();
	
		if(products != null) {
			products.stream().forEach((product)-> {
				try {
					productService.deleteSingleProduct(product.getProductId());;
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		
		
		
		Account account = user.getAccount();
		List<UserRole> userRoles = account.getUserRoles();
		if(userRoles != null) {
			userRoles.stream().forEach((role) -> {
				userRoleRepository.delete(role);
			});
		}
		userRepo.delete(user);
		accountRepo.delete(account);
		return;
	}
}
