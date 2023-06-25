package com.jumpstart.com.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sign_up_role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRole {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sign_up_role_id")
private Long signUpRoleId;
	@Column(unique = true)
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
	private String email;
	private Date addedDate;
	private Long roleId;
	@OneToOne(mappedBy = "signUpRole", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private RoleBasedDetails RoleBasedDetails;
	@Column(name = "password")
	private String password;
	private String token;
}
