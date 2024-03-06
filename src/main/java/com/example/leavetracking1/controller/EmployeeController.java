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
import com.example.leavetracking1.payload.ResponseOutput;
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
    public ResponseEntity<ResponseOutput> applyForLeave(
            @PathVariable(name="userId") Long employeeId,
            @RequestBody LeaveApplicationDto leaveApplicationDto,
            Authentication authentication
            ){
    	ResponseOutput responseOutput;
    	try {
    		String loggedMail=authentication.getName();
        	
        	Long loggedId=employeeLeaveApplicationService.getUserIdByEmail(loggedMail);
        	
        	//checking whether logged user authorized or not
        	if(!loggedId.equals(employeeId)) {
        		responseOutput=new ResponseOutput("failed",null,"Unauthorized user");
        		return new ResponseEntity<>(responseOutput,HttpStatus.UNAUTHORIZED);
        	}
            logger.info("Received request to apply for leave - userId: {}, leaveApplicationDto: {}", employeeId, leaveApplicationDto);

            if(leaveApplicationDto.getStartDate()==null || leaveApplicationDto.getStartDate().toString().trim().isEmpty() ||
            		leaveApplicationDto.getEndDate()==null || leaveApplicationDto.getEndDate().toString().trim().isEmpty() ||
            		leaveApplicationDto.getReason()==null || leaveApplicationDto.getReason().isEmpty() ||
            		leaveApplicationDto.getType()==null || leaveApplicationDto.getType().toString().isEmpty()) {
    		        
    		        String missingField = null;

    		        if (leaveApplicationDto.getStartDate()==null || leaveApplicationDto.getStartDate().toString().trim().isEmpty() ) {
    		            missingField = "StartDate";
    		        } else if (leaveApplicationDto.getEndDate()==null || leaveApplicationDto.getEndDate().toString().trim().isEmpty()) {
    		            missingField = "EndDate";
    		        } else if(leaveApplicationDto.getReason()==null || leaveApplicationDto.getReason().isEmpty()){
    		            missingField = "Reason";
    		        }else
    		        	missingField = "Type";

    		        responseOutput = new ResponseOutput("Failed", null, "Field (" + missingField + ") is mandatory. for apply Leave");
    		        return new ResponseEntity<>(responseOutput, HttpStatus.BAD_REQUEST);
    		    }
            
            // Apply for leave using the EmployeeLeaveApplicationService
            LeaveApplicationStatusDto createdLeaveDto = employeeLeaveApplicationService.applyForLeave(employeeId, leaveApplicationDto);
            if(createdLeaveDto==null)
            {
            	logger.info("Cannot Apply Leave as Max Leaves exhausted or more duration than available - leaveApplicationDto: {}", createdLeaveDto);
            	responseOutput=new ResponseOutput("Failed",null,"Employee acannot apply for leave - maximum leaves exhausted or more duration than available ");
            }
            else {
            	logger.info("Leave application submitted successfully - leaveApplicationDto: {}", createdLeaveDto);
            
            	responseOutput=new ResponseOutput("Success",null,"Employee SuccessFully applied for leave");
            }

            // Return the created leave application and HTTP status 201 (CREATED)
            return new ResponseEntity<>(responseOutput, HttpStatus.CREATED);
    	}catch (Exception e) {
            logger.error("Error while applying leave applications", e);
            responseOutput=new ResponseOutput("failed",null,e.getMessage());
            return new ResponseEntity<>(responseOutput,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Endpoint to get all leaves for a specific user
    @GetMapping("{userId}/leaves")
    public ResponseEntity<ResponseOutput> getAllLeaves(
    		@PathVariable(name="userId") Long employeeId,
    		Authentication authentication
    		){
    	ResponseOutput responseOutput;
    	try {
    		
    		String loggedMail=authentication.getName(); 
        	
        	Long loggedId=employeeLeaveApplicationService.getUserIdByEmail(loggedMail);
        	
        	//checking whether logged user authorized or not
        	if(!loggedId.equals(employeeId)) {
        		responseOutput=new ResponseOutput("failed",null,"Unauthorized request");
        		return new ResponseEntity<>(responseOutput,HttpStatus.UNAUTHORIZED);
        	}
            logger.info("Received request to get all leaves - userId: {}", employeeId);

            // Get all leave applications for the specified user using the EmployeeLeaveApplicationService
            List<UpdatedLeaveStatusDto> allLeaves = employeeLeaveApplicationService.getAllLeaveApplications(employeeId);

            logger.info("Retrieved {} leave applications", allLeaves.size());

            responseOutput=new ResponseOutput("Success",allLeaves,"Successfully retrieved all leaves");
            // Return the list of leave applications and HTTP status 200 (OK)
            return new ResponseEntity<>(responseOutput, HttpStatus.OK);
    	}catch (Exception e) {
            logger.error("Error while fetching leave applications", e);
            
            responseOutput=new ResponseOutput("failed",null,e.getMessage());
            return new ResponseEntity<>(responseOutput,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Endpoint to get a specific leave application for a user
    @GetMapping("{userId}/leaves/{leaveId}")
    public ResponseEntity<ResponseOutput> getLeave(
            @PathVariable(name="userId") Long employeeId,
            @PathVariable(name="leaveId") Long leaveId,
            Authentication authentication
            ){
    	ResponseOutput responseOutput;
    	try {
    		String loggedMail=authentication.getName();
        	
        	Long loggedId=employeeLeaveApplicationService.getUserIdByEmail(loggedMail);
        	
        	if(!loggedId.equals(employeeId)) {
        		responseOutput=new ResponseOutput("failed",null,"Unauthorized request");
        		return new ResponseEntity<>(responseOutput,HttpStatus.UNAUTHORIZED);
        	}
        	
            logger.info("Received request to get leave - userId: {}, leaveId: {}", employeeId, leaveId);

            // Get a specific leave application by leaveId for the specified user using the EmployeeLeaveApplicationService
            UpdatedLeaveStatusDto leave = employeeLeaveApplicationService.getLeaveApplicationById(employeeId, leaveId);

            logger.info("Retrieved leave application - leaveApplicationDto: {}", leave);

            responseOutput=new ResponseOutput("Success",leave,"Successfully retrieved leave");
            // Return the retrieved leave application and HTTP status 200 (OK)
            return new ResponseEntity<>(responseOutput, HttpStatus.OK);
    	}catch (Exception e) {
            logger.error("Error while approving leave applications", e);
            responseOutput=new ResponseOutput("failed",null,e.getMessage());
            return new ResponseEntity<>(responseOutput,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
