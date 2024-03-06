package com.example.leavetracking1.service;

import java.util.List;

import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.payload.ManagerDto;

public interface ManagerService {
	public ManagerDto createManager(ManagerDto managerDto);
	public List<Users> getAllManagers();
}
