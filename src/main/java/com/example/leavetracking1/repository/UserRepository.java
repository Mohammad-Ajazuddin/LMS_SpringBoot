package com.example.leavetracking1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.leavetracking1.entity.Role;
import com.example.leavetracking1.entity.Users;

public interface UserRepository  extends JpaRepository<Users,Long>{

	Optional<Users> findByEmail(String email);

	List<Users> findAllByRole(Role employee);

}
