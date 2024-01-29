package com.example.leavetracking1.payload;

import lombok.Data;

@Data
public class LeaveStatusUpdate {	
	private String comment;
	private boolean status;
}
