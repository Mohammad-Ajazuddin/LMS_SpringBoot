package com.example.leavetracking1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.leavetracking1.entity.LeaveApplication;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication,Long> {

	List<LeaveApplication> findByManagerIdAndEmployeeId(Long managerId,Long employeeId);
	
	Optional<LeaveApplication> findByIdAndEmployeeId(Long leaveId, Long employeeId);

	//to get employee leaves under this manager
	List<LeaveApplication> findAllByManagerId(Long managerId);

	Optional<LeaveApplication> findByIdAndEmployeeIdAndManagerId(Long leaveApplicationId, Long employeeId, Long managerId);

	List<LeaveApplication> findByEmployeeId(Long employeeId);
}
