package com.example.leavetracking1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class LeaveTypes {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	
	@Column(name="leavetype")
    @Enumerated(EnumType.STRING)
	LeaveType leaveType;
	
	@Column(name="maximumleaves")
	int maximumLeaves;

}
