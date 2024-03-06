package com.example.leavetracking1.serviceImpl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.leavetracking1.entity.Role;
import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.exceptions.APIException;
import com.example.leavetracking1.payload.ManagerDto;
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
    public ManagerDto createManager(ManagerDto managerDto) {
        try {
        	if(userRepo.findByEmail(managerDto.getEmail()).isPresent()) {
        		throw new APIException("User with "+managerDto.getEmail()+" mail already exist");
        	}
        	
        	
        	
            logger.info("Creating manager: {}", managerDto);

            managerDto.setPassword(passwordEncoder.encode(managerDto.getPassword()));
            
            Users manager = modelMapper.map(managerDto, Users.class);
            manager.setRole(Role.MANAGER);

            logger.debug("Saving manager: {}", manager);

            Users savedManager = userRepo.save(manager);

            logger.info("Manager created: {}", savedManager);

            return modelMapper.map(savedManager, ManagerDto.class);
        } catch (Exception e) {
            logger.error("Error while creating manager", e);
            throw new APIException(e.getMessage());
        }
    }

	@Override
	public List<Users> getAllManagers() {
		List<Users> managers = userRepo.findAllByRole(Role.MANAGER);
		return managers;
	}
}
