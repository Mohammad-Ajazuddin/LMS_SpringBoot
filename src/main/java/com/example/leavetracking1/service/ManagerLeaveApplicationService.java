package com.example.leavetracking1.service;

import java.util.List;

import com.example.leavetracking1.entity.LeaveApplication;
import com.example.leavetracking1.payload.LeaveApplicationDto;
import com.example.leavetracking1.payload.LeaveApplicationStatusDto;
import com.example.leavetracking1.payload.LeaveStatusUpdate;
import com.example.leavetracking1.payload.UpdatedLeaveStatusDto;

public interface ManagerLeaveApplicationService {
	
	public List<UpdatedLeaveStatusDto> getAllLeaveApplications(Long managerId);
	
	public  List<UpdatedLeaveStatusDto> getAllLeavesByEmployee(Long managerId,Long employeeId);
	
	public UpdatedLeaveStatusDto getLeaveApplicationById(Long managerId,Long employeeId,Long LeaveApplicationId);
	
	public UpdatedLeaveStatusDto updateLeaveApplication(Long managerId,Long employeeId,Long LeaveApplicationId, LeaveStatusUpdate leaveStatusUpdate);
	
	public long getUserIdByEmail(String loggedEmail);
}
