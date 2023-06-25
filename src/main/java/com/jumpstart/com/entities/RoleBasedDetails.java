package com.jumpstart.com.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_based_details")
public class RoleBasedDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roleBasedDetailsId;
	@NotBlank(message = "Name cannot be blank")
	@Size(min = 2, max = 20, message = "min 2 and max 20 characters are allowed")
	private String name;
	private String about;
	@Size(min = 11, message = "min 11 and characters are allowed")
	private String phone_number;
	private String birth;
	private String gender;
	private String profile_image;
	private String district;
	private String address;
	private String typeOfVehicles;
	private String jumpStartId;
	private Long storeId;
	private	String token;
	
	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "sign_up_role_id", referencedColumnName = "sign_up_role_id")
	private SignUpRole signUpRole;

}
