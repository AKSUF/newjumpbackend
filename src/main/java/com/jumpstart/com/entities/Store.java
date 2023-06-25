package com.jumpstart.com.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "store")
@NoArgsConstructor
@Getter
@Setter
public class Store {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long store_id;
	private String store_name;
	private String store_address;
	private String store_desc;
	private String store_image;
	private String category;
	private String availableDays;
	private String opens;
	private String closes;
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	private List<Product> products = new ArrayList<>();
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;
	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(mappedBy = "role", cascade = CascadeType.MERGE)
	private List<UserRole> userRole;
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	private List<UserStore> userStores = new ArrayList<>();

//	@LazyCollection(LazyCollectionOption.TRUE)
//	@OneToMany(mappedBy = "role", cascade = CascadeType.MERGE)
//	private List<UserStore> userStores;
	public List<Account> getAccounts() {
		List<Account> accounts = new ArrayList<>();
		for (UserStore userStore : userStores) {
			accounts.add(userStore.getAccount());
		}
		return accounts;
	}
}
