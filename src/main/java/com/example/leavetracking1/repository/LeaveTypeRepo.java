package com.example.leavetracking1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.leavetracking1.entity.LeaveType;
import com.example.leavetracking1.entity.LeaveTypes;

public interface LeaveTypeRepo extends JpaRepository<LeaveTypes,Long>{

	LeaveTypes findByLeaveType(LeaveType leaveType);

}
