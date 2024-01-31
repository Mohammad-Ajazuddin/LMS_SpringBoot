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
import com.example.leavetracking1.service.ManagerService;

@Service
public class ManagerServiceImpl implements ManagerService {

    public static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto createManager(UserDto userDto) {
        try {
        	
        	if(userRepo.findByEmail(userDto.getEmail())!=null) {
        		throw new APIException("User with "+userDto.getEmail()+" mail already exist");
        	}
            logger.info("Creating manager: {}", userDto);

            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

            Users manager = modelMapper.map(userDto, Users.class);
            manager.setRole(Role.MANAGER);

            logger.debug("Saving manager: {}", manager);

            Users savedManager = userRepo.save(manager);

            logger.info("Manager created: {}", savedManager);

            return modelMapper.map(savedManager, UserDto.class);
        } catch (Exception e) {
            logger.error("Error while creating manager", e);
            throw new APIException(e.getMessage());
        }
    }
}
