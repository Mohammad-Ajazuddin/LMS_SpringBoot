package com.example.leavetracking1.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="leave_info_view")
public class leave_info_view {
	@Id
	private Long user_id;
	private String name;
    private int leaves_applied;
    private int leaves_pending;
    private int leaves_approved;
    private int leaves_rejected;
    private int approved_earned_leaves;
    private int approved_without_pay_leaves;
    private int approved_sick_leaves;
    private int approved_casual_leaves;
    private int approved_paternity_leaves;
    private int rejected_earned_leaves;
    private int rejected_without_pay_leaves;
    private int rejected_sick_leaves;
    private int rejected_casual_leaves;
    private int rejected_paternity_leaves;
    private int remaining_earned_leaves;
    private int remaining_without_pay_leaves;
    private int remaining_sick_leaves;
    private int remaining_casual_leaves;
    private int remaining_paternity_leaves;
    private LocalDate start_date;
    private LocalDate end_date;
    private int manager_id;
}