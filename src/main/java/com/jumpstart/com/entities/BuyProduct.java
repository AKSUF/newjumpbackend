package com.jumpstart.com.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "buy_product")
@Getter
@Setter
@NoArgsConstructor
public class BuyProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "buy_product_id")
	private Long buyProductId;
	@Column(name = "product_name")
	private String productName;
	@Column(name = "how_much")
	private int howMuch;
	@Column(name = "status")
	private String status;
	private double price;
	private double totalPrice;
	@Column(name = "buy_product_img")
	private String image;
	private String deliveryStatus;

	private String message;
	private boolean accepted;
	@Column(name = "producer_id")
	private Long producerId;
	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;
}
