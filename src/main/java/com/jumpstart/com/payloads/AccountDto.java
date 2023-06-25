package com.jumpstart.com.payloads;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {
	private Long account_id;

	private String email;

	private String password;
	private Date addedDate;
	private String provider;
	private UserDto user;
}
