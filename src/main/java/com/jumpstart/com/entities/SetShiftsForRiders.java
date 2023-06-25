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
@Table(name = "set_shifts_for_riders")
@Getter
@Setter
@NoArgsConstructor
public class SetShiftsForRiders {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "set_shifts_for_riders_id")
	private Long setShiftsForRidersId;
	private String shiftsToken;
	private String startTime;
	private String endTime;
	private String district;
	private String address;
	private String date;
	private String shiftStatus;
	private Long riderId;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;
 
}
