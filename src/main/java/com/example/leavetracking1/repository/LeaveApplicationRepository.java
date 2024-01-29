package com.example.leavetracking1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.leavetracking1.entity.LeaveApplication;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication,Long> {

	List<LeaveApplication> findByEmployeeId(Long employeeId);
	
	Optional<LeaveApplication> findByIdAndEmployeeId(Long leaveId, Long employeeId);
}
