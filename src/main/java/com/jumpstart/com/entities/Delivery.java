package com.jumpstart.com.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delivery")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "delivery_id")
	private Long delivery_id;
	@Column(name = "delivery_number")
	private Integer delivery_number;
	@Column(name = "delivery_status")
	private String status;
	private String payment_type;
	private int qty;
	private double amount;
	private String paymentIntentId;
	private String paidOrPayable;
	private Date orderDate;
	private Date standeredDeliveryDate;
	private String deliveryRequest;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "product_id")
	private Product product;
	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "delivery")
	private RiderDelivery riderDelivery;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_details_id", referencedColumnName = "delivery_details_id")
	private DeliveryDetails deliveryDetails;

}
