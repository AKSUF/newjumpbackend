package com.jumpstart.com.payloads;

import java.util.Date;

import javax.persistence.Column;
import javax.validation.constraints.Email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRoleDto {
	private Long signUpRoleId;
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
	@Column(unique = true)
	private String email;
	private Date addedDate;

	private Long roleId;

	private String password;
	private String token;
}
