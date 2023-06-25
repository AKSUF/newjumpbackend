package com.jumpstart.com.payloads;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserStoreDto {
	private Long store_id;
	@NotBlank
	@Size(min = 2, message = "Min size of store name is 2")
	private String store_name;
	@NotBlank
	@Size(min = 5, message = "Min size of store address is 5")
	private String store_address;
	@NotBlank
	@Size(min = 10, message = "Min size of store description is 10")
	private String store_desc;
	private String store_image;
	private String category;
	private String availableDays;
	private String opens;
	private String closes;
	private UserDto user;
}
