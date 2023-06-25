package com.jumpstart.com.payloads;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BuyProductDto {
	private Long buyProductId;
	private String productName;
	private int howMuch;
	private double price;
	private double totalPrice;
	private String status;
	private String message;
	private String deliveryStatus;
	private Long producerId;
	private boolean accepted;
	private UserDto user;
	private String image;
	private StoreDto store;
}
