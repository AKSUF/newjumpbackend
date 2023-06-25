package com.jumpstart.com.payloads;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddtoCartDto {
	private Long cart_id;

	private ProductDto product;

	private int qty;
	private double price;
	private UserDto user;
	private Date addedDate;
	private String added_date;
}
