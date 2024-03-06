package com.example.leavetracking1.service;

import com.example.leavetracking1.entity.LeaveType;
import com.example.leavetracking1.payload.LeaveTypesDto;

public interface LeaveTypesService {
	public LeaveTypesDto addLeaveType(LeaveTypesDto leaveTypesDto) throws Exception;
	public LeaveTypesDto updateLeaveType(LeaveTypesDto leaveTypesDto) throws Exception;
}
