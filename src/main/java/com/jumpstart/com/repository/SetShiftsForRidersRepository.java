package com.jumpstart.com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jumpstart.com.entities.SetShiftsForRiders;
import com.jumpstart.com.entities.User;

public interface SetShiftsForRidersRepository extends JpaRepository<SetShiftsForRiders, Long> {
	Optional<SetShiftsForRiders> findByShiftsToken(String shifttoken);

	List<SetShiftsForRiders> findByUser(User user);
	
	List<SetShiftsForRiders> findByDistrict(String userDistrict);

	List<SetShiftsForRiders> findByRiderId(Long user);
}
