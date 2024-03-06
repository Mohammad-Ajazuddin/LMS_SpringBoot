package com.example.leavetracking1.serviceImpl;

import java.time.Period;
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
import com.example.leavetracking1.repository.LeaveTypeRepo;
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
    
    @Autowired
    private LeaveTypeRepo leaveTypeRepo;

    @Override
    public List<UpdatedLeaveStatusDto> getAllLeaveApplications(Long managerId) {
        try {
            // Fetch all leave applications
            List<LeaveApplication> leaveApplications = leaveApplicationRepo.findAllByManagerId(managerId);

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
            List<LeaveApplication> leaveApplications = leaveApplicationRepo.findByManagerIdAndEmployeeId(managerId,employeeId);

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
            LeaveApplication leaveApplication = leaveApplicationRepo.findByIdAndEmployeeIdAndManagerId(leaveApplicationId, employeeId, managerId)
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
            LeaveApplication leaveApplication = leaveApplicationRepo.findByIdAndEmployeeIdAndManagerId(leaveApplicationId, employeeId, managerId)
                    .orElseThrow(() -> new LeaveNotFound(String.format(
                            "Leave application with id %d for employee id %d not found", leaveApplicationId, employeeId)));
            
            Users employee = userRepo.findById(employeeId).get();

            if (leaveApplication.getStatus() == LeaveStatus.PENDING) {
            	
            	 Period period = Period.between(leaveApplication.getStartDate(), leaveApplication.getEndDate());

                 int days = period.getDays();
            	
            	
                if (leaveStatusUpdate.isStatus()) {
                    // Update leave application status to APPROVED
                	
                	
                	
                    leaveApplication.setComment(leaveStatusUpdate.getComment());
                    leaveApplication.setStatus(LeaveStatus.APPROVED);
                    
//                    employee.setLeavesApproved(employee.getLeavesApproved()+1);//overall
//                    
//                    employee.setLeavesPending(employee.getLeavesPending()-1);
                    
                    employee.setLeavesApproved(employee.getLeavesApproved()+days);//overall
                    
                    employee.setLeavesPending(employee.getLeavesPending()-days);
                    
                    setApprovedLeavesAndRemainingLeaves(leaveApplication.getType(),employee,days);//individual
                    
                    
                    leaveStatusUpdate.setType(leaveApplication.getType().toString());
                    
                    // Update leave application type based on input
//                    switch (leaveStatusUpdate.getType()) {
//                        case "sick":
//                            leaveApplication.setType(LeaveType.SICK_LEAVE);
//                            break;
//                        case "personal":
//                            leaveApplication.setType(LeaveType.PERSONAL_LEAVE);
//                            break;
//                        case "vacation":
//                            leaveApplication.setType(LeaveType.VACATION);
//                            break;
//                        default:
//                            leaveApplication.setType(LeaveType.PERSONAL_LEAVE);
//                    }

                } else {
                    // Update leave application status to REJECTED
                    leaveApplication.setComment(leaveStatusUpdate.getComment());
                    leaveApplication.setStatus(LeaveStatus.REJECTED);
                    
                    employee.setLeavesRejected(employee.getLeavesRejected()+days);//overall
                    employee.setLeavesPending(employee.getLeavesPending()-days);
                    
                    setRejectedLeaves(leaveApplication.getType(),employee, days);//individual leave type count
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
  	
  	
  	private void setApprovedLeavesAndRemainingLeaves(LeaveType leaveType,Users employee,int days)
  	{
  		if(LeaveType.SICK_LEAVE.equals(leaveType))
  		{
  			employee.setApprovedSickLeaves(employee.getApprovedSickLeaves()+days);
  			employee.setRemainingSickLeaves(leaveTypeRepo.findByLeaveType(leaveType).getMaximumLeaves()-employee.getApprovedSickLeaves());
  		}
  		else if(LeaveType.CASUAL_LEAVE.equals(leaveType))
  		{
  			employee.setApprovedCasualLeaves(employee.getApprovedCasualLeaves()+days);
  			employee.setRemainingCasualLeaves(leaveTypeRepo.findByLeaveType(leaveType).getMaximumLeaves()-employee.getApprovedCasualLeaves());
  		}
  		else if(LeaveType.EARNED_LEAVE.equals(leaveType))
  		{
  			employee.setApprovedEarnedLeaves(employee.getApprovedEarnedLeaves()+days);
  			employee.setRemainingEarnedLeaves(leaveTypeRepo.findByLeaveType(leaveType).getMaximumLeaves()-employee.getApprovedEarnedLeaves());
  		}
  		else if(LeaveType.LEAVE_WITHOUT_PAY.equals(leaveType))
  		{
  			employee.setApprovedWithoutPayLeaves(employee.getApprovedWithoutPayLeaves()+days);
  			employee.setRemainingWithoutPayLeaves(leaveTypeRepo.findByLeaveType(leaveType).getMaximumLeaves()-employee.getApprovedWithoutPayLeaves());
  		}
  		else if(LeaveType.PATERNITY_LEAVE.equals(leaveType))
  		{
  			employee.setApprovedPaternityLeaves(employee.getApprovedPaternityLeaves()+days);
  			employee.setRemainingPaternityLeaves(leaveTypeRepo.findByLeaveType(leaveType).getMaximumLeaves()-employee.getApprovedPaternityLeaves());
  		}
  	}
  	
  	private void setRejectedLeaves(LeaveType leaveType,Users employee, int days)
  	{
  		if(LeaveType.SICK_LEAVE.equals(leaveType))
  		{
  			employee.setRejectedSickLeaves(employee.getRejectedSickLeaves()+days);
   		}
  		else if(LeaveType.CASUAL_LEAVE.equals(leaveType))
  		{
  			employee.setRejectedCasualLeaves(employee.getRejectedCasualLeaves()+days);
  		}
  		else if(LeaveType.EARNED_LEAVE.equals(leaveType))
  		{
  			employee.setRejectedEarnedLeaves(employee.getRejectedEarnedLeaves()+days);
  		}
  		else if(LeaveType.LEAVE_WITHOUT_PAY.equals(leaveType))
  		{
  			employee.setRejectedWithoutPayLeaves(employee.getRejectedWithoutPayLeaves()+days);
  		}
  		else if(LeaveType.PATERNITY_LEAVE.equals(leaveType))
  		{
  			employee.setRejectedPaternityLeaves(employee.getRejectedPaternityLeaves()+days);
  		}
  	}
}
