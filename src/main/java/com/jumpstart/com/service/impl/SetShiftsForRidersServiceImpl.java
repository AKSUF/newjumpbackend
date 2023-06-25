package com.jumpstart.com.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumpstart.com.entities.Account;
import com.jumpstart.com.entities.SetShiftsForRiders;
import com.jumpstart.com.entities.User;
import com.jumpstart.com.exception.ResourceNotFoundException;
import com.jumpstart.com.payloads.SetShiftsForRidersDto;
import com.jumpstart.com.repository.AccountRepository;
import com.jumpstart.com.repository.SetShiftsForRidersRepository;
import com.jumpstart.com.service.SetShiftsForRiderService;
import com.jumpstart.com.status.ShiftStatus;
import com.jumpstart.com.utils.JwtUtils;

@Service
public class SetShiftsForRidersServiceImpl implements SetShiftsForRiderService {

	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private SetShiftsForRidersRepository setShiftsForRidersRepository;
	@Autowired
	private AccountRepository accountRepository;
	
	// Employee will set shift for riders
	@Override
	public SetShiftsForRiders setShiftForRiders(SetShiftsForRidersDto setShiftsForRidersDto, String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    SetShiftsForRiders setShiftsForRiders = this.modelMapper.map(setShiftsForRidersDto, SetShiftsForRiders.class);
	    setShiftsForRiders.setShiftsToken(UUID.randomUUID().toString());
	    setShiftsForRiders.setUser(account.getUser());
	    setShiftsForRiders.setShiftStatus(ShiftStatus.Available.name());
	    return this.setShiftsForRidersRepository.save(setShiftsForRiders);
	}
	

	// employee will get all his/her shifts
	@Override
	public List<SetShiftsForRidersDto> getAllShifts() {
		List<SetShiftsForRiders> setShiftsForRiders = this.setShiftsForRidersRepository.findAll();
		List<SetShiftsForRidersDto> setShiftsForRidersDtos=setShiftsForRiders.stream().map((s)->
		this.modelMapper.map(s, SetShiftsForRidersDto.class)).collect(Collectors.toList());
		return setShiftsForRidersDtos;
	}
	
	@Override
	public SetShiftsForRidersDto getsingleShift(String shifttoken) {
		SetShiftsForRiders setShiftsForRiders = this.setShiftsForRidersRepository.findByShiftsToken(shifttoken)
				.orElseThrow(() -> new ResourceNotFoundException("Set Shifts For Riders", "Set Shifts For Riders id",shifttoken));
		return this.modelMapper.map(setShiftsForRiders, SetShiftsForRidersDto.class);
	}
	
	// employee can delete shifts
	@Override
	public void deleteShift(String shifttoken) {
		SetShiftsForRiders setShiftsForRiders = this.setShiftsForRidersRepository.findByShiftsToken(shifttoken)
				.orElseThrow(() -> new ResourceNotFoundException("Set Shifts For Riders", "Set Shifts For Riders id",shifttoken));
		this.setShiftsForRidersRepository.delete(setShiftsForRiders);
		
	}
	
	// employee can edit shifts
	@Override
	public SetShiftsForRidersDto updateShift(SetShiftsForRidersDto setShiftsForRidersDto,String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepository.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    SetShiftsForRiders setShiftsForRiders = this.setShiftsForRidersRepository.findByShiftsToken(setShiftsForRidersDto.getShiftsToken())
	            .orElseThrow(() -> new ResourceNotFoundException("Set Shifts For Riders", "Set Shifts For Riders id",setShiftsForRidersDto.getShiftsToken()));
	    // Update the properties of the existing entity with the new values
	    setShiftsForRiders.setStartTime(setShiftsForRidersDto.getStartTime());
	    setShiftsForRiders.setEndTime(setShiftsForRidersDto.getEndTime());
	    setShiftsForRiders.setAddress(setShiftsForRidersDto.getAddress());;
	    setShiftsForRiders.setDistrict(setShiftsForRidersDto.getDistrict());;
	    setShiftsForRiders.setShiftStatus(setShiftsForRidersDto.getShiftStatus());;
	    setShiftsForRiders.setShiftsToken(setShiftsForRidersDto.getShiftsToken());
	    setShiftsForRiders.setDate(setShiftsForRidersDto.getDate());
	    setShiftsForRiders.setUser(account.getUser());
	    SetShiftsForRiders updateSetShiftsForRiders = this.setShiftsForRidersRepository.save(setShiftsForRiders);
	    return this.modelMapper.map(updateSetShiftsForRiders, SetShiftsForRidersDto.class);
	}
	
	@Override
	public List<SetShiftsForRidersDto> getUserShifts(String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepository.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

	    User user = account.getUser();
	    List<SetShiftsForRiders> setShiftsForRiders = this.setShiftsForRidersRepository.findByUser(user);
	   
	    List<SetShiftsForRidersDto> setShiftsForRidersDtos = setShiftsForRiders.stream()
	            .map(product -> this.modelMapper.map(product, SetShiftsForRidersDto.class))
	            .collect(Collectors.toList());
	    return setShiftsForRidersDtos;
	}

	
	// Get all available district based rider shifts
	@Override
	public List<SetShiftsForRidersDto> getAllShiftsForRider(String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    User user = account.getUser();
	  
	    String userDistrict = user.getDistrict();
	    List<SetShiftsForRiders> setShiftsForRiders = this.setShiftsForRidersRepository.findByDistrict(userDistrict);
	    List<SetShiftsForRidersDto> setShiftsForRidersDtos = setShiftsForRiders.stream().map((s) ->
	            this.modelMapper.map(s, SetShiftsForRidersDto.class)).collect(Collectors.toList());
	    return setShiftsForRidersDtos;
	}
	
	// Get all available district based rider id
	@Override
	public List<SetShiftsForRidersDto> getAllShiftsbasedRiderId(String token) {
	    String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    Long user = account.getAccount_id();
	  
	    List<SetShiftsForRiders> setShiftsForRiders = this.setShiftsForRidersRepository.findByRiderId(user);
	    List<SetShiftsForRidersDto> setShiftsForRidersDtos = setShiftsForRiders.stream().map((s) ->
	            this.modelMapper.map(s, SetShiftsForRidersDto.class)).collect(Collectors.toList());
	    return setShiftsForRidersDtos;
	}
	
	// rider can take available shift 
	@Override
	public SetShiftsForRidersDto shiftStatusTaken(String shifttoken, String token) {
		SetShiftsForRiders shiftsForRiders = this.setShiftsForRidersRepository.findByShiftsToken(shifttoken)
				.orElseThrow(() -> new ResourceNotFoundException("Set Shifts For Riders", "Set Shifts For Riders id",shifttoken));
		String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    shiftsForRiders.setRiderId(account.getAccount_id());
	    shiftsForRiders.setShiftStatus(ShiftStatus.Taken.name());
        SetShiftsForRiders changedStatus = this.setShiftsForRidersRepository.save(shiftsForRiders);
		return this.modelMapper.map(changedStatus, SetShiftsForRidersDto.class);
	}
	
	// rider can Swap his / her shift 
	@Override
	public SetShiftsForRidersDto shiftStatusOfferSwap(String shifttoken, String token) {
		SetShiftsForRiders shiftsForRiders = this.setShiftsForRidersRepository.findByShiftsToken(shifttoken)
				.orElseThrow(() -> new ResourceNotFoundException("Set Shifts For Riders", "Set Shifts For Riders id",shifttoken));
		String email = jwtUtils.getUserNameFromToken(token);
	    Account account = accountRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
	    shiftsForRiders.setRiderId(account.getAccount_id());
	    shiftsForRiders.setShiftStatus(ShiftStatus.Offer_swap.name());
        SetShiftsForRiders changedStatus = this.setShiftsForRidersRepository.save(shiftsForRiders);
		return this.modelMapper.map(changedStatus, SetShiftsForRidersDto.class);
	}
	

	
}
