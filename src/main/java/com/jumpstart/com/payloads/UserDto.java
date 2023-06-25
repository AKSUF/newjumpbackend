package com.jumpstart.com.payloads;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
public class UserDto {
	private Long user_id;

	@NotEmpty(message = "Name can not be empty")
	@Size(max = 100, min = 3, message = "Name must be between 3 to 100 characters")
	private String name;

	@NotEmpty(message = "Biography can not be empty")
	@Size(max = 250, min = 15, message = "Biography must be between 15 to 250 characters")
	private String about;

	@NotEmpty(message = "Phone can not be empty")
	@Size(max = 20, min = 9, message = "Phone must be between 9 to 20 characters")
	private String phone_number;

	@NotEmpty(message = "Date of birth can not be empty")
	private String birth;

	@NotEmpty(message = "Gender can not be empty")
	private String gender;

	private String profile_image;
	private String district;
	private String address;
	private String typeOfVehicles;
	private String JumpStartId;
}
