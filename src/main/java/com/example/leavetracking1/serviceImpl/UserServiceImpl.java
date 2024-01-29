package com.example.leavetracking1.serviceImpl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.leavetracking1.entity.Role;
import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.payload.UserDto;
import com.example.leavetracking1.repository.UserRepository;
import com.example.leavetracking1.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Service method to create an employee
    @Override
    public UserDto createEmployee(UserDto userDto) {

        logger.info("Creating employee: {}", userDto);

        // Encode the password before saving it to the database
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        // Map the UserDto to the Users entity
        Users employee = modelMapper.map(userDto, Users.class);

        // Set the role for the employee (assuming Role.EMPLOYEE)
        employee.setRole(Role.EMPLOYEE);

        logger.debug("Saving employee: {}", employee);

        // Save the employee to the database
        Users savedEmployee = userRepository.save(employee);

        logger.info("Employee created: {}", savedEmployee);

        // Map the saved employee back to UserDto and return it
        return modelMapper.map(savedEmployee, UserDto.class);
    }
}
