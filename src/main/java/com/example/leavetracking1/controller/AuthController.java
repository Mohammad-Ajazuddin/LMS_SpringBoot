package com.example.leavetracking1.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leavetracking1.entity.JWTAuthResponse;
import com.example.leavetracking1.entity.LoginDto;
import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.payload.ManagerDto;
import com.example.leavetracking1.payload.ResponseOutput;
import com.example.leavetracking1.payload.UserDto;
import com.example.leavetracking1.repository.UserRepository;
import com.example.leavetracking1.security.JwtTokenProvider;
import com.example.leavetracking1.service.UserService;
import com.example.leavetracking1.service.ManagerService;


//@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepo;
   
    @Autowired
    private ManagerService managerService;
    
    // End point for creating an employee
    @PostMapping("/register/employee")
    public ResponseEntity<ResponseOutput> createUserEmployee(@RequestBody UserDto userDto){
    	ResponseOutput responseOutput;
    	try {
    		
    		if(userDto.getName() == null || userDto.getName().isEmpty() ||
    		        userDto.getEmail() == null || userDto.getEmail().isEmpty() ||
    		        userDto.getPassword() == null || userDto.getPassword().isEmpty()||
    		        userDto.getMobile()==null|| userDto.getMobile().isEmpty()||
    		        userDto.getDateOfJoining()==null||userDto.getDateOfJoining().toString().trim().isEmpty() ||
    		        userDto.getManagerId()==null) {
    		        
    		        String missingField = null;

    		        if (userDto.getName() == null || userDto.getName().isEmpty()) {
    		            missingField = "name";
    		        } else if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
    		            missingField = "email";
    		        } else if(userDto.getPassword() == null || userDto.getPassword().isEmpty()){
    		            missingField = "password";
    		        }else if(userDto.getMobile()==null|| userDto.getMobile().isEmpty()) {
    		        	missingField = "mobile";
    		        }else if(userDto.getDateOfJoining()==null || userDto.getDateOfJoining().toString().trim().isEmpty()){
    		        	missingField = "datOfJoining";
    		        }else {
    		        	missingField = "managerId";
    		        }

    		        responseOutput = new ResponseOutput("failed", null, "Field (" + missingField + ") is mandatory. Failed to register employee");
    		        return new ResponseEntity<>(responseOutput, HttpStatus.BAD_REQUEST);
    		    }
        	
            logger.info("Received request to create employee: {}", userDto);

            // Create employee using UserService
            UserDto createdEmployee = userService.createEmployee(userDto);

            logger.info("Employee created successfully: {}", createdEmployee);
            
            responseOutput= new ResponseOutput("Success",null,"Successfully registerd employee");
            
            // Return the created employee and HTTP status 201 (CREATED)
            return new ResponseEntity<>(responseOutput, HttpStatus.CREATED);
    	}catch(Exception e) {
    		logger.info("Error while registering employee");
    		responseOutput= new ResponseOutput("failed",null,e.getMessage());
    		return new ResponseEntity<>(responseOutput,HttpStatus.BAD_REQUEST);
    	}
    }
    
    // Endpoint for creating a manager
    @PostMapping("/register/manager")
    public ResponseEntity<ResponseOutput> createManager(@RequestBody ManagerDto managerDto){
    	
    	ResponseOutput responseOutput;
        try {
        	logger.info("Received request to create manager: {}", managerDto);
        	
            
        	if(managerDto.getName() == null || managerDto.getName().isEmpty() ||
    		        managerDto.getEmail() == null || managerDto.getEmail().isEmpty() ||
    		        managerDto.getPassword() == null || managerDto.getPassword().isEmpty()||
    		        managerDto.getMobile()==null|| managerDto.getMobile().isEmpty()||
    		        managerDto.getDateOfJoining()==null || managerDto.getDateOfJoining().toString().trim().isEmpty()) {
    		        
    		        String missingField = null;

    		        if (managerDto.getName() == null || managerDto.getName().isEmpty()) {
    		            missingField = "name";
    		        } else if (managerDto.getEmail() == null || managerDto.getEmail().isEmpty()) {
    		            missingField = "email";
    		        } else if(managerDto.getPassword() == null || managerDto.getPassword().isEmpty()){
    		            missingField = "password";
    		        }else if(managerDto.getMobile()==null|| managerDto.getMobile().isEmpty()) {
    		        	missingField = "mobile";
    		        }else if(managerDto.getDateOfJoining()==null || managerDto.getDateOfJoining().toString().trim().isEmpty()) {
    		        	missingField = "datOfJoining";
    		        }

    		        responseOutput = new ResponseOutput("failed", null, "Field (" + missingField + ") is mandatory. Failed to register maanager");
    		        return new ResponseEntity<>(responseOutput, HttpStatus.BAD_REQUEST);
    		    }
            // Create manager using ManagerService
            ManagerDto createdManager = managerService.createManager(managerDto);

            logger.info("Manager created successfully: {}",createdManager);
            
            responseOutput= new ResponseOutput("Success",null,"Successfully registerd manager");

            // Return the created manager and HTTP status 201 (CREATED)
            return new ResponseEntity<>(responseOutput, HttpStatus.CREATED);
        }catch(Exception e) {
        	logger.info("Error while registering manager");
    		responseOutput= new ResponseOutput("failed",null,e.getMessage());
    		return new ResponseEntity<>(responseOutput,HttpStatus.BAD_REQUEST);
        }
    }
    
    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<ResponseOutput> loginuser(@RequestBody LoginDto loginDto){
        
    	ResponseOutput responseOutput;
        try {
        	if( loginDto.getEmail() == null || loginDto.getEmail().isEmpty() ||
    		        loginDto.getPassword() == null || loginDto.getPassword().isEmpty()) {
    		        
    		        String missingField = null;

    		        if (loginDto.getEmail() == null || loginDto.getEmail().isEmpty()) {
    		            missingField = "email";
    		        } else {
    		            missingField = "password";
    		        }

    		        responseOutput = new ResponseOutput("failed", null, "Field (" + missingField + ") is mandatory. Failed to login");
    		        return new ResponseEntity<>(responseOutput, HttpStatus.BAD_REQUEST);
    		    }
            // Authenticate the user using Spring Security's AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            
            // Set the authentication in the SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate a JWT token using JwtTokenProvider
            String token = jwtTokenProvider.generateToken(authentication);
            
            Users currentUser = userRepo.findByEmail(loginDto.getEmail()).get();

            
            Object[] details = {new JWTAuthResponse(token),currentUser};
            
//            responseOutput= new ResponseOutput("Success",new JWTAuthResponse(token),"User Logged in Succeessful"); 
            responseOutput= new ResponseOutput("Success",details,"User Logged in Succeessful"); 
            
            // Return the JWT token in a JWTAuthResponse and HTTP status 200 (OK)
            return ResponseEntity.ok(responseOutput);
            
        } catch (Exception e) {
            // Log the exception (You might want to log it more appropriately)
            logger.error("Exception during login: {}", e);
            
            responseOutput= new ResponseOutput("failed",null,e.getMessage());
            // Return HTTP status 400 (BAD REQUEST) for failed login
            return new ResponseEntity<>(responseOutput,HttpStatus.UNAUTHORIZED);
        }
    }
    
    @GetMapping("/getAllManagers")
    public ResponseEntity<ResponseOutput> getAllManagers()
    {
    	List<Users> allManagers = managerService.getAllManagers();
    	ResponseOutput responseOutput = new ResponseOutput("Success",allManagers,"All managers retrieved");
    	
    	return new ResponseEntity<>(responseOutput,HttpStatus.OK);
    	
    }
    
//    @PostMapping("/getCurrentUser")
//    public ResponseEntity<ResponseOutput> getRole(String email)
//    {
//    	Users currentUser;
//    	try {
//        currentUser = userRepo.findByEmail(email).get();
//    	}
//    	catch(Exception e) {
//        	ResponseOutput responseOutput = new ResponseOutput("Failed",null,e.getMessage());
//        	return new ResponseEntity<>(responseOutput,HttpStatus.NOT_FOUND);
//        }
//    	
//    	ResponseOutput responseOutput = new ResponseOutput("Success",currentUser,"Current user retrieved");
//    	
//    	return new ResponseEntity<>(responseOutput,HttpStatus.OK);
//    	
//    }
}
