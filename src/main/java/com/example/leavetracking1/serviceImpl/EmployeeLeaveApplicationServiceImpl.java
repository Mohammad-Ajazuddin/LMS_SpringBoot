
package com.example.leavetracking1.serviceImpl;

import java.time.Period;
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
import com.example.leavetracking1.repository.LeaveTypeRepo;
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
    
    @Autowired
    private LeaveTypeRepo leaveTypeRepo;

    @Override
    public LeaveApplicationStatusDto applyForLeave(Long employeeId, LeaveApplicationDto leaveApplicationDto) {
        try {
            logger.info("Applying for leave - Employee ID: {}, Leave Application DTO: {}", employeeId, leaveApplicationDto);

            Users employee = userRepo.findById(employeeId).orElseThrow(
                    () -> new UserNotFound(String.format("Employee with ID %d not found", employeeId))
            );
            

            LeaveApplication leaveApplication = modelMapper.map(leaveApplicationDto, LeaveApplication.class);
            
            Period period = Period.between(leaveApplication.getStartDate(), leaveApplication.getEndDate());

            int days = period.getDays();

            
        if(leaveApplication.getType().equals(LeaveType.SICK_LEAVE) && 
        		employee.getApprovedSickLeaves()>= leaveTypeRepo.findByLeaveType(LeaveType.SICK_LEAVE).getMaximumLeaves() &&
        			employee.getRemainingSickLeaves()<days)
        {
        	logger.info("Maximum claims for sick leaves or more duration than available - Employee ID: {}, Leave Application DTO: {}", employeeId, leaveApplicationDto);
        	return null;
        }
        else if(leaveApplication.getType().equals(LeaveType.CASUAL_LEAVE) && 
        		employee.getApprovedCasualLeaves()>= leaveTypeRepo.findByLeaveType(LeaveType.CASUAL_LEAVE).getMaximumLeaves() &&
        			employee.getRemainingCasualLeaves()<days)
        {
        	logger.info("Maximum claims for Casual leaves or more duration than available - Employee ID: {}, Leave Application DTO: {}", employeeId, leaveApplicationDto);
        	return null;
        }
        else if(leaveApplication.getType().equals(LeaveType.EARNED_LEAVE) && 
        		employee.getApprovedEarnedLeaves()>= leaveTypeRepo.findByLeaveType(LeaveType.EARNED_LEAVE).getMaximumLeaves() &&
        			employee.getRemainingEarnedLeaves()<days)
        {
        	logger.info("Maximum claims for Earned leaves or more duration than available - Employee ID: {}, Leave Application DTO: {}", employeeId, leaveApplicationDto);
        	return null;
        }
        else if(leaveApplication.getType().equals(LeaveType.LEAVE_WITHOUT_PAY) && 
        		employee.getApprovedWithoutPayLeaves()>= leaveTypeRepo.findByLeaveType(LeaveType.LEAVE_WITHOUT_PAY).getMaximumLeaves() &&
        			employee.getRemainingWithoutPayLeaves()<days)
        {
        	logger.info("Maximum claims for Without Pay leaves or more duration than available - Employee ID: {}, Leave Application DTO: {}", employeeId, leaveApplicationDto);
        	return null;
        }
        else if(leaveApplication.getType().equals(LeaveType.PATERNITY_LEAVE) && 
        		employee.getApprovedPaternityLeaves()>= leaveTypeRepo.findByLeaveType(LeaveType.PATERNITY_LEAVE).getMaximumLeaves() &&
        			employee.getRemainingSickLeaves()<days)
        {
        	logger.info("Maximum claims for Paternity leaves or more duration than available - Employee ID: {}, Leave Application DTO: {}", employeeId, leaveApplicationDto);
        	return null;
        }
        else {
            leaveApplication.setStatus(LeaveStatus.PENDING);
//            employee.setLeavesPending(employee.getLeavesPending()+1);
//            employee.setLeavesApplied(employee.getLeavesApplied()+1);
            
            employee.setLeavesPending(employee.getLeavesPending()+days);
            employee.setLeavesApplied(employee.getLeavesApplied()+days);
            
            
            leaveApplication.setEmployee(employee);
            
            //Setting the manager under which this employee is.
            //So leave can be approved by this manager/ this leave application is visible to only this manager
            leaveApplication.setManagerId(employee.getManager().getId());
         
//            if(leaveApplicationDto.getType().equalsIgnoreCase("SICK_LEAVE"))
//            {
//            	leaveApplication.setType(LeaveType.SICK_LEAVE);
//            }
//            else if(leaveApplicationDto.getType().equalsIgnoreCase("EARNED_LEAVE"))
//            {
//            	leaveApplication.setType(LeaveType.EARNED_LEAVE);
//            }
//            else if(leaveApplicationDto.getType().equalsIgnoreCase("LEAVE_WITHOUT_PAY"))
//            {
//            	leaveApplication.setType(LeaveType.LEAVE_WITHOUT_PAY);
//            }
//            else
//            	leaveApplication.setType(LeaveType.PATERNITY_LEAVE);
            
            leaveApplication.setType(leaveApplicationDto.getType());

            LeaveApplication createdLeave = leaveApplicationRepo.save(leaveApplication);

            logger.info("Leave application submitted successfully - Employee ID: {}, Leave Application ID: {}",
                    employeeId, createdLeave.getId());

            LeaveApplicationStatusDto leaveApplicationStatusDto = modelMapper.map(createdLeave, LeaveApplicationStatusDto.class);
            leaveApplicationStatusDto.setEmployeeId(employeeId);

            return leaveApplicationStatusDto;
            }
        } catch (Exception e) {
            logger.error("Error while applying for leave", e);
            throw new APIException(e.getMessage());
        }
    }

    @Override
    public List<UpdatedLeaveStatusDto> getAllLeaveApplications(Long employeeId) {
        try {
            logger.info("Fetching all leave applications - Employee ID: {}", employeeId);

            List<LeaveApplication> leavesApplications = leaveApplicationRepo.findByEmployeeId(employeeId);

            List<UpdatedLeaveStatusDto> leaveApplicationStatusDtos = leavesApplications.stream()
                    .map(this::convertToUpdateLeaveDto)
                    .collect(Collectors.toList());

            logger.info("Retrieved {} leave applications - Employee ID: {}", leaveApplicationStatusDtos.size(), employeeId);

            return leaveApplicationStatusDtos;
        } catch (Exception e) {
            logger.error("Error while fetching all leave applications", e);
            throw new APIException(e.getMessage());
        }
    }

    @Override
    public UpdatedLeaveStatusDto getLeaveApplicationById(Long employeeId, Long leaveApplicationId) {
        try {
            logger.info("Fetching leave application by ID - Employee ID: {}, Leave Application ID: {}", employeeId, leaveApplicationId);

            LeaveApplication leavesApplication = leaveApplicationRepo.findByIdAndEmployeeId(leaveApplicationId, employeeId)
                    .orElseThrow(() -> new LeaveNotFound(String.format(
                            "Leave application with ID %d for employee ID %d not found", leaveApplicationId, employeeId)));

            return convertToUpdateLeaveDto(leavesApplication);
        } catch (Exception e) {
            logger.error("Error while fetching leave application by ID", e);
            throw new APIException(e.getMessage());
        }
    }

    @Override
    public long getUserIdByEmail(String loggedEmail) {
        try {
            logger.info("Fetching user ID by email - Email: {}", loggedEmail);

            Users user = userRepo.findByEmail(loggedEmail).orElseThrow(
                    () -> new UserNotFound(String.format("Employee with email %s not found", loggedEmail))
            );

            return user.getId();
        } catch (Exception e) {
            logger.error("Error while fetching user ID by email", e);
            throw new APIException(e.getMessage());
        }
    }

    private UpdatedLeaveStatusDto convertToUpdateLeaveDto(LeaveApplication leaveApplication) {
        return modelMapper.map(leaveApplication, UpdatedLeaveStatusDto.class);
    }
}
