package com.jumpstart.com.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryDetailsDto {
	private Long delivery_details_id;
	private String delivery_address;
	private String name;
	private String phone_number;
	private String district;
	

	private UserDto user;

}
