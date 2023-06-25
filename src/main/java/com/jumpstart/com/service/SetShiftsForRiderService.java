package com.jumpstart.com.service;

import java.util.List;

import com.jumpstart.com.entities.SetShiftsForRiders;
import com.jumpstart.com.payloads.SetShiftsForRidersDto;

public interface SetShiftsForRiderService {
	SetShiftsForRiders setShiftForRiders(SetShiftsForRidersDto setShiftsForRidersDto, String token);

	List<SetShiftsForRidersDto> getAllShifts();

	SetShiftsForRidersDto getsingleShift(String token);

	SetShiftsForRidersDto updateShift(SetShiftsForRidersDto setShiftsForRidersDto, String token);

	List<SetShiftsForRidersDto> getUserShifts(String token);

	void deleteShift(String shifttoken);

	List<SetShiftsForRidersDto> getAllShiftsForRider(String token);

	SetShiftsForRidersDto shiftStatusTaken(String shifttoken, String token);

	SetShiftsForRidersDto shiftStatusOfferSwap(String shifttoken, String token);

	List<SetShiftsForRidersDto> getAllShiftsbasedRiderId(String token);
}
