package com.example.leavetracking1.service;

import java.util.List;

import com.example.leavetracking1.payload.LeaveApplicationDto;
import com.example.leavetracking1.payload.LeaveApplicationStatusDto;
import com.example.leavetracking1.payload.UpdatedLeaveStatusDto;

public interface EmployeeLeaveApplicationService {
	
	public LeaveApplicationStatusDto applyForLeave(Long employeeId,LeaveApplicationDto leaveApplicationDto);
	
	public List<UpdatedLeaveStatusDto> getAllLeaveApplications(Long employeeId);
	
	public UpdatedLeaveStatusDto getLeaveApplicationById(Long employeeId,Long LeaveApplicationId);
	
	public long getUserIdByEmail(String loggedEmail);
}
