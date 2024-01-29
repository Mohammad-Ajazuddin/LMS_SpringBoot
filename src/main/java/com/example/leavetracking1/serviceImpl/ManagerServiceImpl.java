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

    // Service method to create a manager
    @Override
    public UserDto createManager(UserDto userDto) {
        logger.info("Creating manager: {}", userDto);

        // Encode the password before saving it to the database
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Map the UserDto to the Users entity
        Users manager = modelMapper.map(userDto, Users.class);

        // Set the role for the manager (assuming Role.MANAGER)
        manager.setRole(Role.MANAGER);

        logger.debug("Saving manager: {}", manager);

        // Save the manager to the database
        Users savedManager = userRepo.save(manager);

        logger.info("Manager created: {}", savedManager);

        // Map the saved manager back to UserDto and return it
        return modelMapper.map(savedManager, UserDto.class);
    }
}
