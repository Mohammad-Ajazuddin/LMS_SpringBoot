package com.example.leavetracking1.entity;

import lombok.Data;

@Data

public class JWTAuthResponse {
	private String token;
	private String tokenType="Bearer";
	public JWTAuthResponse(String token) {
		this.token=token;
	}
}
