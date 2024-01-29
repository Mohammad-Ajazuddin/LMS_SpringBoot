package com.example.leavetracking1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class LeaveNotFound extends RuntimeException{
	
	private String message;
	
	public LeaveNotFound(String message) {
		super(message);
		this.message=message;
	}
}
