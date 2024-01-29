package com.example.leavetracking1.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class LeaveApplication {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name="startDate", nullable=false)
	 @NotNull(message = "startDate cannot be empty")
	@Future(message = "Date of birth must be in the past")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
	
	@Column(name="endDate", nullable=false)
	@Future(message = "Date of birth must be in the past")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "endDate cannot be empty")
    private LocalDate endDate;
	
	@Column(name="reason", nullable=false)
	 @NotEmpty(message = "reason cannot be empty")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
    
    @Enumerated(EnumType.STRING)
    private LeaveType type;
    
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Users employee;
    
    private String comment;
}
