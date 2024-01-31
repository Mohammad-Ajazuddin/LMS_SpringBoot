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
import com.example.leavetracking1.entity.LeaveType;
import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.exceptions.APIException;
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

    @Override
    public List<UpdatedLeaveStatusDto> getAllLeaveApplications(Long managerId) {
        try {
            // Fetch all leave applications
            List<LeaveApplication> leaveApplications = leaveApplicationRepo.findAll();

            // Convert leave applications to DTOs
            List<UpdatedLeaveStatusDto> updatedLeaveStatusDto = leaveApplications.stream()
                    .map(this::convertToUpdateLeaveDto)
                    .collect(Collectors.toList());

            logger.info("Retrieved {} leave applications for managerId: {}", updatedLeaveStatusDto.size(), managerId);

            return updatedLeaveStatusDto;
        } catch (Exception e) {
            logger.error("Error while fetching all leave applications for manager", e);
            throw new APIException(e.getMessage());
        }
    }

    @Override
    public List<UpdatedLeaveStatusDto> getAllLeavesByEmployee(Long managerId, Long employeeId) {
        try {
            // Fetch leave applications for a specific employee
            List<LeaveApplication> leaveApplications = leaveApplicationRepo.findByEmployeeId(employeeId);

            // Convert leave applications to DTOs
            List<UpdatedLeaveStatusDto> updatedLeaveStatusDto = leaveApplications.stream()
                    .map(this::convertToUpdateLeaveDto)
                    .collect(Collectors.toList());

            logger.info("Retrieved {} leave applications for managerId: {}", updatedLeaveStatusDto.size(), managerId);

            return updatedLeaveStatusDto;
        } catch (Exception e) {
            logger.error("Error while fetching all leave applications for manager and employee", e);
            throw new APIException(e.getMessage());
        }
    }

    @Override
    public UpdatedLeaveStatusDto getLeaveApplicationById(Long managerId, Long employeeId, Long leaveApplicationId) {
        try {
            // Fetch a specific leave application by its ID and employee ID
            LeaveApplication leaveApplication = leaveApplicationRepo.findByIdAndEmployeeId(leaveApplicationId, employeeId)
                    .orElseThrow(() -> new LeaveNotFound(String.format(
                            "Leave application with id %d for employee id %d not found", leaveApplicationId, employeeId)));

            // Convert the leave application to a DTO
            UpdatedLeaveStatusDto updatedLeaveStatusDto = convertToUpdateLeaveDto(leaveApplication);

            logger.info("Retrieved leave application by ID: {} for managerId: {} and employeeId: {}",
                    leaveApplicationId, managerId, employeeId);

            return updatedLeaveStatusDto;
        } catch (Exception e) {
            logger.error("Error while fetching leave application by ID for manager and employee", e);
            throw new APIException(e.getMessage());
        }
    }

    @Override
    public UpdatedLeaveStatusDto updateLeaveApplication(Long managerId, Long employeeId, Long leaveApplicationId, LeaveStatusUpdate leaveStatusUpdate) {
        try {
            // Fetch the leave application by its ID and employee ID
            LeaveApplication leaveApplication = leaveApplicationRepo.findByIdAndEmployeeId(leaveApplicationId, employeeId)
                    .orElseThrow(() -> new LeaveNotFound(String.format(
                            "Leave application with id %d for employee id %d not found", leaveApplicationId, employeeId)));

            if (leaveApplication.getStatus() == LeaveStatus.PENDING) {
                if (leaveStatusUpdate.isStatus()) {
                    // Update leave application status to APPROVED
                	
                    leaveApplication.setComment(leaveStatusUpdate.getComment());
                    leaveApplication.setStatus(LeaveStatus.APPROVED);

                    // Update leave application type based on input
                    switch (leaveStatusUpdate.getType()) {
                        case "sick":
                            leaveApplication.setType(LeaveType.SICK_LEAVE);
                            break;
                        case "personal":
                            leaveApplication.setType(LeaveType.PERSONAL_LEAVE);
                            break;
                        case "vacation":
                            leaveApplication.setType(LeaveType.VACATION);
                            break;
                        default:
                            leaveApplication.setType(LeaveType.PERSONAL_LEAVE);
                    }

                } else {
                    // Update leave application status to REJECTED
                    leaveApplication.setComment(leaveStatusUpdate.getComment());
                    leaveApplication.setStatus(LeaveStatus.REJECTED);
                }

                // Save the updated leave application
                LeaveApplication savedLeaveApplication = leaveApplicationRepo.save(leaveApplication);

                // Convert the saved leave application to DTO
                UpdatedLeaveStatusDto updatedLeaveApplicationStatusDto = convertToUpdateLeaveDto(savedLeaveApplication);

                logger.info("Updated leave application status. Leave application ID: {}, Manager ID: {}, Employee ID: {}",
                        leaveApplicationId, managerId, employeeId);

                return updatedLeaveApplicationStatusDto;
            }

            // If the leave application is not in a pending state, return the updated status
            return convertToUpdateLeaveDto(leaveApplication);
        } catch (Exception e) {
            logger.error("Error while updating leave application status for manager and employee", e);
            throw new APIException(e.getMessage());
        }
    }

    private UpdatedLeaveStatusDto convertToUpdateLeaveDto(LeaveApplication leaveApplication) {
        return modelMapper.map(leaveApplication, UpdatedLeaveStatusDto.class);
    }

  //Get Email from the provided userId
  	@Override
  	public long getUserIdByEmail(String loggedEmail) {
  		try {
  			Users user=userRepo.findByEmail(loggedEmail).orElseThrow(
  	  				()-> new UserNotFound(String.format("Employee with email %s not found", loggedEmail))
  	  				);
  	  				
  	  		return user.getId();
  		}catch(Exception e)
  		{
  			throw new APIException(e.getMessage());
  		}
  	}
}
