package com.jumpstart.com.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
public class Role {
	@Id
	@Column(name = "role_id")
	private Long role_id;

	@Column(name = "role_name")
	private String role_name;

	@LazyCollection(LazyCollectionOption.TRUE)
	@OneToMany(mappedBy = "role", cascade = CascadeType.MERGE)
	private List<UserRole> userRole;
}
