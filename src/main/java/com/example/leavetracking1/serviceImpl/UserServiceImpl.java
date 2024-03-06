package com.example.leavetracking1.serviceImpl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.leavetracking1.entity.LeaveType;
import com.example.leavetracking1.entity.LeaveTypes;
import com.example.leavetracking1.entity.Role;
import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.exceptions.APIException;
import com.example.leavetracking1.payload.UserDto;
import com.example.leavetracking1.payload.LeaveTypesDto;
import com.example.leavetracking1.repository.LeaveTypeRepo;
import com.example.leavetracking1.repository.UserRepository;
import com.example.leavetracking1.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private LeaveTypeRepo leaveTypeRepo;

    @Override
    public UserDto createEmployee(UserDto userDto) {
        try {
            logger.info("Creating employee: {}", userDto);
            
            if(!userRepository.findByEmail(userDto.getEmail()).isEmpty()) {
        		throw new APIException("User with "+userDto.getEmail()+" mail already exist");
        	}

            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

            Users employee = modelMapper.map(userDto, Users.class);
           
            //map manager
            Users manager = userRepository.findById(userDto.getManagerId()).get();
            employee.setManager(manager);
            
            employee.setRole(Role.EMPLOYEE);
            
            LeaveTypes leaveType = leaveTypeRepo.findByLeaveType(LeaveType.SICK_LEAVE);
            //System.out.println(leaveType); //output LeaveTypes(id=1, leaveType=SICK_LEAVE, maximumLeaves=5)
            //System.out.println(leaveType.getMaximumLeaves()); //5
            employee.setRemainingSickLeaves(leaveType.getMaximumLeaves());
            
            leaveType = leaveTypeRepo.findByLeaveType(LeaveType.CASUAL_LEAVE);
            employee.setRemainingCasualLeaves(leaveType.getMaximumLeaves());
            
            leaveType = leaveTypeRepo.findByLeaveType(LeaveType.EARNED_LEAVE);
            employee.setRemainingEarnedLeaves(leaveType.getMaximumLeaves());
            
            leaveType = leaveTypeRepo.findByLeaveType(LeaveType.LEAVE_WITHOUT_PAY);// The issue for null pointer was here as I didn't add this type in db yet!!
            employee.setRemainingWithoutPayLeaves(leaveType.getMaximumLeaves());
            
            leaveType = leaveTypeRepo.findByLeaveType(LeaveType.PATERNITY_LEAVE);
            employee.setRemainingPaternityLeaves(leaveType.getMaximumLeaves());
            
            

            logger.debug("Saving employee: {}", employee);

            Users savedEmployee = userRepository.save(employee);

            logger.info("Employee created: {}", savedEmployee);

            return modelMapper.map(savedEmployee, UserDto.class);
        } 
        catch (Exception e) {
            logger.error("Exception during creating employee", e);
//            System.out.println(e.getStackTrace());
//            for(StackTraceElement ele : e.getStackTrace())
//            {
//            	System.out.println(ele);
//            }
            throw new APIException(e.getMessage());
        }
    }
}
