package com.example.leavetracking1.payload;

import com.example.leavetracking1.entity.LeaveType;

import lombok.Data;

@Data
public class LeaveTypesDto {
	
	private int id;
	private LeaveType leaveType;
	private int maximumLeaves;

}
