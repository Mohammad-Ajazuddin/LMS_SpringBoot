package com.example.leavetracking1.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.leavetracking1.entity.LeaveApplication;
import com.example.leavetracking1.entity.LeaveStatus;
import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.exceptions.LeaveNotFound;
import com.example.leavetracking1.exceptions.UserNotFound;
import com.example.leavetracking1.payload.LeaveApplicationStatusDto;
import com.example.leavetracking1.payload.LeaveStatusUpdate;
import com.example.leavetracking1.payload.UpdatedLeaveStatusDto;
import com.example.leavetracking1.repository.UserRepository;
import com.example.leavetracking1.repository.LeaveApplicationRepository;
import com.example.leavetracking1.service.ManagerLeaveApplicationService;

@Service
public class ManagerLeaveApplicationServiceImpl implements ManagerLeaveApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerLeaveApplicationServiceImpl.class);

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepo;

    // Service method to get all leave applications for a manager and employee
    @Override
    public List<UpdatedLeaveStatusDto> getAllLeaveApplications(Long managerId) {
        // Retrieve all leave applications
        List<LeaveApplication> leaveApplications = leaveApplicationRepo.findAll();

        // Convert leave applications to updatedLeaveStatusDtos
        List<UpdatedLeaveStatusDto> updatedLeaveStatusDto = leaveApplications.stream()
                .map(this::convertToUpdateLeaveDto)
                .collect(Collectors.toList());

        logger.info("Retrieved {} leave applications for managerId: {}",
        		updatedLeaveStatusDto.size(), managerId);

        return updatedLeaveStatusDto;
    }
    
    @Override
	public List<UpdatedLeaveStatusDto> getAllLeavesByEmployee(Long managerId, Long employeeId) {
    	 // Retrieve all leave applications
        List<LeaveApplication> leaveApplications = leaveApplicationRepo.findByEmployeeId(employeeId);

        // Convert leave applications to updatedLeaveStatusDto
        List<UpdatedLeaveStatusDto> updatedLeaveStatusDto = leaveApplications.stream()
                .map(this::convertToUpdateLeaveDto)
                .collect(Collectors.toList());

        logger.info("Retrieved {} leave applications for managerId: {}",
        		updatedLeaveStatusDto.size(), managerId);

        return updatedLeaveStatusDto;
	}
    
    // Service method to get a specific leave application by its ID
    @Override
    public UpdatedLeaveStatusDto getLeaveApplicationById(Long managerId, Long employeeId, Long leaveApplicationId) {
        // Retrieve the leave application by its ID and employee ID
        LeaveApplication leaveApplication = leaveApplicationRepo.findByIdAndEmployeeId(leaveApplicationId, employeeId)
                .orElseThrow(() -> new LeaveNotFound(String.format(
                        "Leave application with id %d for employee id %d not found", leaveApplicationId, employeeId)));

        // Convert the leave application to a updatedLeaveStatusDto
        UpdatedLeaveStatusDto updatedLeaveStatusDto = convertToUpdateLeaveDto(leaveApplication);

        logger.info("Retrieved leave application by ID: {} for managerId: {} and employeeId: {}",
                leaveApplicationId, managerId, employeeId);

        return updatedLeaveStatusDto;
    }

    // Service method to update the status of a leave application
    @Override
    public UpdatedLeaveStatusDto updateLeaveApplication(Long managerId, Long employeeId, Long leaveApplicationId,LeaveStatusUpdate leaveStatusUpdate) {
        // Retrieve the leave application by its ID and employee ID
        LeaveApplication leaveApplication = leaveApplicationRepo.findByIdAndEmployeeId(leaveApplicationId, employeeId)
                .orElseThrow(() -> new LeaveNotFound(String.format(
                        "Leave application with id %d for employee id %d not found", leaveApplicationId, employeeId)));

        // Check if the leave application; is in a pending state
        if (leaveApplication.getStatus() == LeaveStatus.PENDING) 
        {
            if (leaveStatusUpdate.isStatus()) {
            	leaveApplication.setComment(leaveStatusUpdate.getComment());
                leaveApplication.setStatus(LeaveStatus.APPROVED);
                
            } else {
            	leaveApplication.setComment(leaveStatusUpdate.getComment());
                leaveApplication.setStatus(LeaveStatus.REJECTED);
            }

            // Save the updated leave application
            LeaveApplication savedLeaveApplication = leaveApplicationRepo.save(leaveApplication);

            // Convert the saved leave application to a DTO
            UpdatedLeaveStatusDto updatedLeaveApplicationStatusDto = convertToUpdateLeaveDto(savedLeaveApplication);

            logger.info("Updated leave application status. Leave application ID: {}, Manager ID: {}, Employee ID: {}",
                    leaveApplicationId, managerId, employeeId);

            return updatedLeaveApplicationStatusDto;
        }

        // If the leave application is not in a pending state, return the current status
        return convertToUpdateLeaveDto(leaveApplication);
    }

    // Helper method to convert LeaveApplication entity to UpdatedLeaveStatusDto 
    private UpdatedLeaveStatusDto convertToUpdateLeaveDto(LeaveApplication leaveApplication) {
        return modelMapper.map(leaveApplication, UpdatedLeaveStatusDto.class);
    }

    //Get Email from the provided userId
	@Override
	public long getUserIdByEmail(String loggedEmail) {
		Users user=userRepo.findByEmail(loggedEmail).orElseThrow(
				()-> new UserNotFound(String.format("Employee with email %s not found", loggedEmail))
				);
				
		return user.getId();
	}

}
