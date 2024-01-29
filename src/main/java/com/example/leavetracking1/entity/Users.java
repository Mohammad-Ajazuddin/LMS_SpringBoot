package com.example.leavetracking1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	
	@Enumerated(EnumType.STRING)
	private Role role;
}
