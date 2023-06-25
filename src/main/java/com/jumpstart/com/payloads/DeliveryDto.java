package com.jumpstart.com.payloads;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDto {
	private Long delivery_id;

	private Integer delivery_number;

	private String status;

	private String payment_type;
	private double amount;
	private UserDto user;
	private String paidOrPayable;
	private int qty;
	private ProductDto product;
	private DeliveryDetailsDto deliveryDetails;
	private String paymentIntentId;
	private String deliveryRequest;
	private RiderDeliveryDto riderDelivery;
	private Date standeredDeliveryDate;
}
