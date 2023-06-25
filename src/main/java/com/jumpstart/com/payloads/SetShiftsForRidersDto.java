package com.jumpstart.com.payloads;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SetShiftsForRidersDto {
	private Long setShiftsForRidersId;
	private String shiftsToken;
	private String startTime;
	private String endTime;
	private String district;
	private String address;
	private String shiftStatus;
	private UserDto user;
	private String date;
	private Long riderId;
}
