package com.example.leavetracking1.entity;

import java.time.LocalDate;

import com.example.leavetracking1.entity.LeaveTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="user")
public class Users {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="name", nullable=false)
    @NotEmpty(message = "name cannot be empty")
    private String name;
    
    @Email(message = "Please provide a valid email address")
    @Column(name="email", nullable=false)
    @NotEmpty(message = "email cannot be empty")
    private String email;
    
    @Column(name="password", nullable=false)
    @NotEmpty(message = "password cannot be empty")
    private String password;

    @Column(name="mobile")
    private String mobile;
    
    @Column(name="dateofjoining")
    private LocalDate dateOfJoining;
    
    @ManyToOne
    @JoinColumn(name="manager_id")
    private Users manager;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private int leavesApplied = 0;
    private int leavesRejected = 0;
    private int leavesPending = 0;
    private int leavesApproved = 0;
    
    private int approvedEarnedLeaves = 0;
    private int approvedSickLeaves = 0;
    private int approvedCasualLeaves = 0;
    private int approvedPaternityLeaves = 0;
    private int approvedWithoutPayLeaves = 0;
    
    private int rejectedEarnedLeaves = 0;
    private int rejectedSickLeaves = 0;
    private int rejectedCasualLeaves = 0;
    private int rejectedPaternityLeaves = 0;
    private int rejectedWithoutPayLeaves = 0;
    
    private int remainingEarnedLeaves = 0;
    private int remainingSickLeaves = 0;
    private int remainingCasualLeaves = 0;
    private int remainingPaternityLeaves = 0;
    private int remainingWithoutPayLeaves = 0;
}
