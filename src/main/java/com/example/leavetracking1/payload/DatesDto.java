package com.example.leavetracking1.payload;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DatesDto {
	private LocalDate startDate;
	private LocalDate endDate;
}
