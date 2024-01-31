package com.example.leavetracking1.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ResponseOutput {
	private String status;
	private Object data;
	private String message;
}
