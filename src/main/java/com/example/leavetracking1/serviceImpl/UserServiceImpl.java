package com.example.leavetracking1.serviceImpl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.leavetracking1.entity.Role;
import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.exceptions.APIException;
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

    @Override
    public UserDto createEmployee(UserDto userDto) {
        try {
            logger.info("Creating employee: {}", userDto);
            
            if(!userRepository.findByEmail(userDto.getEmail()).isEmpty()) {
        		throw new APIException("User with "+userDto.getEmail()+" mail already exist");
        	}

            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

            Users employee = modelMapper.map(userDto, Users.class);
            employee.setRole(Role.EMPLOYEE);

            logger.debug("Saving employee: {}", employee);

            Users savedEmployee = userRepository.save(employee);

            logger.info("Employee created: {}", savedEmployee);

            return modelMapper.map(savedEmployee, UserDto.class);
        } 
        catch (Exception e) {
            logger.error("Exception during creating employee", e);
            throw new APIException(e.getMessage());
        }
    }
}
