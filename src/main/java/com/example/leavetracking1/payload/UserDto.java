package com.example.leavetracking1.payload;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String mobile;
    private LocalDate dateOfJoining;
    private Long managerId;
}
