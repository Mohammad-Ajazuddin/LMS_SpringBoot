package com.example.leavetracking1.payload;

import java.time.LocalDate;

import com.example.leavetracking1.entity.LeaveStatus;
import com.example.leavetracking1.entity.LeaveType;

import lombok.Data;

@Data

public class LeaveApplicationStatusDto {
	private Long id;
	private LocalDate startDate;
	private LocalDate endDate;
	private String reason;
	private LeaveType Type;
	private LeaveStatus status;
	private Long employeeId;
}