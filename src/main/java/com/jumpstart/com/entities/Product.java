package com.jumpstart.com.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long productId;
	@Column(name = "product_name")
	private String productName;
	@Column(name = "product_desc")
	private String product_desc;
	@Column(name = "category")
	private String category;
	@Column(name = "price")
	private double price;
	@Column(name = "available_quantity")
	private int availableQuantity;
	@Column(name = "status")
	private String status;
	@Column(name = "product_img")
	private String image;
	private Date addedDate;
	private String shippingAddress;
	private double weight;
	private String adminStatus;
	private double dimensions;
	private double charge;
	private boolean approved;
	private String adminMessage;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "product")
	private List<Delivery> delivery;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
	private List<AddtoCart> addToCarts;
	@ManyToOne
	@JoinColumn(name = "store_id")
	private Store store;
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private Set<Comment> comments = new HashSet<>();
}
