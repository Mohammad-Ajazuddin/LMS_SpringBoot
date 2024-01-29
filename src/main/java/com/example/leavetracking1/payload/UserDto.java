package com.example.leavetracking1.payload;

import lombok.Data;

@Data

public class UserDto {
	private Long id;
	private String name;
	private String email;
	private String password;
}
