
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

    @Override
    public LeaveApplicationStatusDto applyForLeave(Long employeeId, LeaveApplicationDto leaveApplicationDto) {
        try {
            logger.info("Applying for leave - Employee ID: {}, Leave Application DTO: {}", employeeId, leaveApplicationDto);

            Users employee = userRepo.findById(employeeId).orElseThrow(
                    () -> new UserNotFound(String.format("Employee with ID %d not found", employeeId))
            );

            LeaveApplication leaveApplication = modelMapper.map(leaveApplicationDto, LeaveApplication.class);
            leaveApplication.setStatus(LeaveStatus.PENDING);
            leaveApplication.setEmployee(employee);
            leaveApplication.setType(LeaveType.NULL);

            LeaveApplication createdLeave = leaveApplicationRepo.save(leaveApplication);

            logger.info("Leave application submitted successfully - Employee ID: {}, Leave Application ID: {}",
                    employeeId, createdLeave.getId());

            LeaveApplicationStatusDto leaveApplicationStatusDto = modelMapper.map(createdLeave, LeaveApplicationStatusDto.class);
            leaveApplicationStatusDto.setEmployeeId(employeeId);

            return leaveApplicationStatusDto;
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
