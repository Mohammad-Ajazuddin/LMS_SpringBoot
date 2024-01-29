package com.example.leavetracking1.entity;

public enum Role {

	EMPLOYEE,
	MANAGER
	;
	
	public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
