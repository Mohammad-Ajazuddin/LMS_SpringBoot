package com.example.leavetracking1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class APIException extends RuntimeException{
	
	private String message;
	
	public APIException(String message) {
		super(message);
		this.message=message;
	}
}
