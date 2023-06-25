package com.jumpstart.com.payloads;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProductDto {
	private Long productId;
	private String productName;
	private String product_desc;
	@Column(name = "category")
	private String category;
	private String status;
	private int availableQuantity;
	private double price;
	@Column(name = "product_img")
	private String image;
	private StoreDto store;
	private Date addedDate;
	private String shippingAddress;
	private double weight;
	private double dimensions;
	private double charge;
	private UserDto user;
	private boolean approved;
	private String adminStatus;
	private String adminMessage;
	private Set<CommentDto> comments = new HashSet<>();
}
