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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.leavetracking1.entity.LeaveApplication;
import com.example.leavetracking1.payload.LeaveApplicationDto;
import com.example.leavetracking1.payload.LeaveApplicationStatusDto;
import com.example.leavetracking1.payload.LeaveStatusUpdate;
import com.example.leavetracking1.payload.UpdatedLeaveStatusDto;
import com.example.leavetracking1.service.EmployeeLeaveApplicationService;
import com.example.leavetracking1.service.ManagerLeaveApplicationService;

@RestController
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {
	
	private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    private EmployeeLeaveApplicationService employeeLeaveApplicationService;
    
    @Autowired
    private ManagerLeaveApplicationService managerLeaveApplicationService;
    
    // Endpoint to get all leave applications 
    @GetMapping("{managerId}/leaves")
    public ResponseEntity<List<UpdatedLeaveStatusDto>> getAllLeaves(
            @PathVariable(name="managerId") Long managerId,
            Authentication authentication
    ){
    	try {
    		String loggedMail=authentication.getName();
        	
        	Long loggedId=managerLeaveApplicationService.getUserIdByEmail(loggedMail);
        	
        	//checking whether logged user authorized or not
        	if(!loggedId.equals(managerId)) {
        		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        	}
        	
            logger.info("Fetching all leave applications for managerId: {}", managerId);
            
         // Retrieve all leave applications for the specified employee under the manager
            List<UpdatedLeaveStatusDto> allLeaves = managerLeaveApplicationService.getAllLeaveApplications(managerId);
            logger.info("Fetched {} leave applications successfully", allLeaves.size());
            
         // Return the list of leave applications and HTTP status 200 (OK)
            return new ResponseEntity<>(allLeaves, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while fetching leave applications", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
 // Endpoint to get all leave applications for a specific employee under a manager
    @GetMapping("{managerId}/leaves/{userId}")
    public ResponseEntity<List<UpdatedLeaveStatusDto>> getAllLeavesByEmployee(
            @PathVariable(name="managerId") Long managerId,
            @PathVariable(name="userId") Long employeeId,
            Authentication authentication
    ){
    	try {
    		String loggedMail=authentication.getName();
        	
        	Long loggedId=managerLeaveApplicationService.getUserIdByEmail(loggedMail);
        	//checking whether logged user authorized or not
        	if(!loggedId.equals(managerId)) {
        		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        	}
        	
            logger.info("Fetching all leave applications for managerId: {}", managerId);
            
         // Retrieve all leave applications for the specified employee under the manager
            List<UpdatedLeaveStatusDto> allLeaves = managerLeaveApplicationService.getAllLeavesByEmployee(managerId,employeeId);
            logger.info("Fetched {} leave applications successfully", allLeaves.size());
            
         // Return the list of leave applications and HTTP status 200 (OK)
            return new ResponseEntity<>(allLeaves, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while fetching leave applications", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Endpoint to get a specific leave application for an employee under a manager
    @GetMapping("{managerId}/leaves/{employeeId}/leave/{leaveId}")
    public ResponseEntity<UpdatedLeaveStatusDto> getLeave(
            @PathVariable(name="managerId") Long managerId,
            @PathVariable(name="employeeId") Long employeeId,
            @PathVariable(name="leaveId") Long leaveId,
            Authentication authentication
    ){
    	try {
    		
    		String loggedMail=authentication.getName();
        	
        	Long loggedId=managerLeaveApplicationService.getUserIdByEmail(loggedMail);
        	//checking whether logged user authorized or not
        	if(!loggedId.equals(managerId)) {
        		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        	}
           
    		logger.info("Fetching leave application with id {} for managerId: {} and employeeId: {}", leaveId, managerId, employeeId);
    		
    		// Retrieve a specific leave application for the specified employee under the manager
    		UpdatedLeaveStatusDto leave = managerLeaveApplicationService.getLeaveApplicationById(managerId, employeeId, leaveId);
            logger.info("Fetched leave application successfully");
            
            // Return the retrieved leave application and HTTP status 200 (OK)
            return new ResponseEntity<>(leave, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while fetching leave application", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Endpoint to approve leave for an employee under a manager
    @PutMapping("{managerId}/leaves/{employeeId}/checkLeave/{leaveId}")
    public ResponseEntity<UpdatedLeaveStatusDto> upadateLeave(
            @PathVariable(name="managerId") Long managerId,
            @PathVariable(name="employeeId") Long employeeId,
            @PathVariable(name="leaveId") Long leaveId,
            @RequestBody LeaveStatusUpdate leaveStatusUpdate,
            Authentication authentication
    ) {
    	try {
    		
    		String loggedMail=authentication.getName();
        	
        	Long loggedId=managerLeaveApplicationService.getUserIdByEmail(loggedMail);
        	//checking whether logged user authorized or not
        	if(!loggedId.equals(managerId)) {
        		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        	}
           
    		logger.info("Approving leave application with id {} for managerId: {} and employeeId: {}", leaveId, managerId, employeeId);
            // Update the leave application status to approved for the specified employee under the manager
    		UpdatedLeaveStatusDto updatedLeaveStatusDto = managerLeaveApplicationService.updateLeaveApplication(managerId, employeeId, leaveId,leaveStatusUpdate);

            logger.info("Leave application approved successfully");
            // Return the updated leave application and HTTP status 200 (OK)
            return new ResponseEntity<>(updatedLeaveStatusDto,HttpStatus.OK);
    	}catch (Exception e) {
            logger.error("Error while approving leave application", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
