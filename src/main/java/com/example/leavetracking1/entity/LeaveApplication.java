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
import jakarta.validation.constraints.FutureOrPresent;
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
	
	@Column(name="start_date", nullable=false)
	@NotNull(message = "Start date cannot be empty")
	@FutureOrPresent(message = "Start date must be in the present or future")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
	
	@Column(name="end_date", nullable=false)
	@NotNull(message = "End date cannot be empty")
	@FutureOrPresent(message = "End date must be in the present or future")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
	
	@Column(name="reason", nullable=false)
	@NotEmpty(message = "Reason cannot be empty")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name="leave_status")
    private LeaveStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name="leave_type")
    private LeaveType Type;
    
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Users employee;
    
    @Column(name = "manager_id")
    private Long managerId;
    
    @Column
    private String comment;
}
