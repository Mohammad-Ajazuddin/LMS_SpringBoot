package com.example.leavetracking1.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leavetracking1.entity.LeaveApplication;
import com.example.leavetracking1.payload.LeaveApplicationDto;
import com.example.leavetracking1.payload.LeaveApplicationStatusDto;
import com.example.leavetracking1.payload.UpdatedLeaveStatusDto;
import com.example.leavetracking1.service.EmployeeLeaveApplicationService;

@RestController
@RequestMapping("/employee")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeLeaveApplicationService employeeLeaveApplicationService;
    
    // Endpoint for an employee to apply for leave
    @PostMapping("/{userId}/apply")
    public ResponseEntity<LeaveApplicationStatusDto> applyForLeave(
            @PathVariable(name="userId") Long employeeId,
            @RequestBody LeaveApplicationDto leaveApplicationDto,
            Authentication authentication
            ){
    	
    	try {
    		String loggedMail=authentication.getName();
        	
        	Long loggedId=employeeLeaveApplicationService.getUserIdByEmail(loggedMail);
        	
        	//checking whether logged user authorized or not
        	if(!loggedId.equals(employeeId)) {
        		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        	}
            logger.info("Received request to apply for leave - userId: {}, leaveApplicationDto: {}", employeeId, leaveApplicationDto);

            // Apply for leave using the EmployeeLeaveApplicationService
            LeaveApplicationStatusDto createdLeaveDto = employeeLeaveApplicationService.applyForLeave(employeeId, leaveApplicationDto);

            logger.info("Leave application submitted successfully - leaveApplicationDto: {}", createdLeaveDto);

            // Return the created leave application and HTTP status 201 (CREATED)
            return new ResponseEntity<>(createdLeaveDto, HttpStatus.CREATED);
    	}catch (Exception e) {
            logger.error("Error while applying leave applications", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Endpoint to get all leaves for a specific user
    @GetMapping("{userId}/leaves")
    public ResponseEntity<List<UpdatedLeaveStatusDto>> getAllLeaves(
    		@PathVariable(name="userId") Long employeeId,
    		Authentication authentication
    		){
    	
    	try {
    		
    		String loggedMail=authentication.getName(); 
        	
        	Long loggedId=employeeLeaveApplicationService.getUserIdByEmail(loggedMail);
        	
        	//checking whether logged user authorized or not
        	if(!loggedId.equals(employeeId)) {
        		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        	}
            logger.info("Received request to get all leaves - userId: {}", employeeId);

            // Get all leave applications for the specified user using the EmployeeLeaveApplicationService
            List<UpdatedLeaveStatusDto> allLeaves = employeeLeaveApplicationService.getAllLeaveApplications(employeeId);

            logger.info("Retrieved {} leave applications", allLeaves.size());

            // Return the list of leave applications and HTTP status 200 (OK)
            return new ResponseEntity<>(allLeaves, HttpStatus.OK);
    	}catch (Exception e) {
            logger.error("Error while fetching leave applications", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Endpoint to get a specific leave application for a user
    @GetMapping("{userId}/leaves/{leaveId}")
    public ResponseEntity<UpdatedLeaveStatusDto> getLeave(
            @PathVariable(name="userId") Long employeeId,
            @PathVariable(name="leaveId") Long leaveId,
            Authentication authentication
            ){
    	try {
    		String loggedMail=authentication.getName();
        	
        	Long loggedId=employeeLeaveApplicationService.getUserIdByEmail(loggedMail);
        	
        	if(!loggedId.equals(employeeId)) {
        		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        	}
        	
            logger.info("Received request to get leave - userId: {}, leaveId: {}", employeeId, leaveId);

            // Get a specific leave application by leaveId for the specified user using the EmployeeLeaveApplicationService
            UpdatedLeaveStatusDto leave = employeeLeaveApplicationService.getLeaveApplicationById(employeeId, leaveId);

            logger.info("Retrieved leave application - leaveApplicationDto: {}", leave);

            // Return the retrieved leave application and HTTP status 200 (OK)
            return new ResponseEntity<>(leave, HttpStatus.OK);
    	}catch (Exception e) {
            logger.error("Error while approving leave applications", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
