package com.jumpstart.com.payloads;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RoleBasedDetailsDto {
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
	private SignUpRoleDto signUpRole;
}
