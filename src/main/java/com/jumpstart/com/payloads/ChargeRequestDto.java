package com.jumpstart.com.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargeRequestDto {
	private Long charge_request_id;
    private int amount;
    	private DeliveryDto delivery;
    	 private String paymentIntentId;
}
