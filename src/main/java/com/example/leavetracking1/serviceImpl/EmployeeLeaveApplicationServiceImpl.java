package com.example.leavetracking1.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.entity.LeaveApplication;
import com.example.leavetracking1.entity.LeaveStatus;
import com.example.leavetracking1.entity.LeaveType;
import com.example.leavetracking1.exceptions.APIException;
import com.example.leavetracking1.exceptions.LeaveNotFound;
import com.example.leavetracking1.exceptions.UserNotFound;
import com.example.leavetracking1.payload.LeaveApplicationDto;
import com.example.leavetracking1.payload.LeaveApplicationStatusDto;
import com.example.leavetracking1.payload.UpdatedLeaveStatusDto;
import com.example.leavetracking1.repository.UserRepository;
import com.example.leavetracking1.repository.LeaveApplicationRepository;
import com.example.leavetracking1.service.EmployeeLeaveApplicationService;

@Service
public class EmployeeLeaveApplicationServiceImpl implements EmployeeLeaveApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveApplicationServiceImpl.class);

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepo;
    
    // Service method to apply for leave
    @Override
    public LeaveApplicationStatusDto applyForLeave(Long employeeId, LeaveApplicationDto leaveApplicationDto) {
        try {
        	logger.info("Applying for leave - Employee ID: {}, Leave Application DTO: {}", employeeId, leaveApplicationDto);

            // Retrieve the employee by ID
            Users employee = userRepo.findById(employeeId).orElseThrow(
                    () -> new UserNotFound(String.format("Employee with ID %d not found", employeeId))
            );
            logger.info("user details fetched from repository");

            // Map the DTO to LeaveApplication entity
            LeaveApplication leaveApplication = modelMapper.map(leaveApplicationDto, LeaveApplication.class);
            leaveApplication.setStatus(LeaveStatus.PENDING);
            leaveApplication.setEmployee(employee);
            
            logger.info("leave application created by adding details");
            // Save the created leave application
            
            //intializing with null
            leaveApplication.setType(LeaveType.NULL);
            LeaveApplication createdLeave = leaveApplicationRepo.save(leaveApplication);

            logger.info("Leave application submitted successfully - Employee ID: {}, Leave Application ID: {}",
                    employeeId, createdLeave.getId());

            LeaveApplicationStatusDto leaveApplicationStatusDto=modelMapper.map(createdLeave, LeaveApplicationStatusDto.class);
            leaveApplicationStatusDto.setEmployeeId(employeeId);
            // Convert and return the created leave application as DTO
            return leaveApplicationStatusDto;
        }catch(Exception e) {
        	logger.info("Error while saving leave application");
        	throw new APIException("exception occered in saving leave"+e);
        }
    }

    // Service method to get all leave applications for an employee
    @Override
    public List<UpdatedLeaveStatusDto> getAllLeaveApplications(Long employeeId) {
        logger.info("Fetching all leave applications - Employee ID: {}", employeeId);

        // Retrieve all leave applications for the employee
        List<LeaveApplication> leavesApplications = leaveApplicationRepo.findByEmployeeId(employeeId);

        // Convert leave applications to DTOs
        List<UpdatedLeaveStatusDto> leaveApplicationStatusDtos = leavesApplications.stream()
                .map(this::convertToUpdateLeaveDto)
                .collect(Collectors.toList());

        logger.info("Retrieved {} leave applications - Employee ID: {}", leaveApplicationStatusDtos.size(), employeeId);

        // Return the list of leave application DTOs
        return leaveApplicationStatusDtos;
    }

    // Service method to get a specific leave application by its ID
    @Override
    public UpdatedLeaveStatusDto getLeaveApplicationById(Long employeeId, Long leaveApplicationId) {
        logger.info("Fetching leave application by ID - Employee ID: {}, Leave Application ID: {}", employeeId, leaveApplicationId);

        // Retrieve the leave application by its ID and employee ID
        LeaveApplication leavesApplication = leaveApplicationRepo.findByIdAndEmployeeId(leaveApplicationId, employeeId)
                .orElseThrow(() -> new LeaveNotFound(String.format(
                        "Leave application with ID %d for employee ID %d not found", leaveApplicationId, employeeId)));

        // Convert the leave application to a DTO
        return convertToUpdateLeaveDto(leavesApplication);
    }

    // Helper method to convert LeaveApplication entity to DTO
    private LeaveApplicationStatusDto convertToDto(LeaveApplication leaveApplication) {
        return modelMapper.map(leaveApplication, LeaveApplicationStatusDto.class);
    }

 // Helper method to convert LeaveApplication entity to UpdatedLeaveStatusDto 
    private UpdatedLeaveStatusDto convertToUpdateLeaveDto(LeaveApplication leaveApplication) {
        return modelMapper.map(leaveApplication, UpdatedLeaveStatusDto.class);
    }
    
	@Override
	public long getUserIdByEmail(String loggedEmail) {
		
		
		Users user=userRepo.findByEmail(loggedEmail).orElseThrow(
				()-> new UserNotFound(String.format("Employee with email %s not found", loggedEmail))
				);
				
		return user.getId();
	}
    
    
}
