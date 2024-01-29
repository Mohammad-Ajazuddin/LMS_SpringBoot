package com.example.leavetracking1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leavetracking1.entity.JWTAuthResponse;
import com.example.leavetracking1.entity.LoginDto;
import com.example.leavetracking1.payload.UserDto;
import com.example.leavetracking1.security.JwtTokenProvider;
import com.example.leavetracking1.service.UserService;
import com.example.leavetracking1.service.ManagerService;

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
    private ManagerService managerService;
    
    // Endpoint for creating an employee
    @PostMapping("/register/employee")
    public ResponseEntity<UserDto> createUserEmployee(@RequestBody UserDto userDto){
    	
        logger.info("Received request to create employee: {}", userDto);

        // Create employee using UserService
        UserDto createdEmployee = userService.createEmployee(userDto);

        logger.info("Employee created successfully: {}", createdEmployee);

        // Return the created employee and HTTP status 201 (CREATED)
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }
    
    // Endpoint for creating a manager
    @PostMapping("/register/manager")
    public ResponseEntity<UserDto> createManager(@RequestBody UserDto userDto){
    	
        logger.info("Received request to create manager: {}", userDto);

        // Create manager using ManagerService
        UserDto createdManager = managerService.createManager(userDto);

        logger.info("Manager created successfully");

        // Return the created manager and HTTP status 201 (CREATED)
        return new ResponseEntity<>(createdManager, HttpStatus.CREATED);
    }
    
    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> loginuser(@RequestBody LoginDto loginDto){
        
        try {
            // Authenticate the user using Spring Security's AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            
            // Set the authentication in the SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate a JWT token using JwtTokenProvider
            String token = jwtTokenProvider.generateToken(authentication);
            
            // Return the JWT token in a JWTAuthResponse and HTTP status 200 (OK)
            return ResponseEntity.ok(new JWTAuthResponse(token));
            
        } catch (Exception e) {
            // Log the exception (You might want to log it more appropriately)
            logger.error("Exception during login: {}", e);
            
            // Return HTTP status 400 (BAD REQUEST) for failed login
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
