package com.example.leavetracking1.serviceImpl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.leavetracking1.entity.LeaveType;
import com.example.leavetracking1.entity.LeaveTypes;
import com.example.leavetracking1.entity.Role;
import com.example.leavetracking1.entity.Users;
import com.example.leavetracking1.exceptions.APIException;
import com.example.leavetracking1.payload.LeaveTypesDto;
import com.example.leavetracking1.repository.LeaveTypeRepo;
import com.example.leavetracking1.repository.UserRepository;
import com.example.leavetracking1.service.LeaveTypesService;

import java.util.List;

@Service
public class LeaveTypesServiceImpl implements LeaveTypesService{
	
    public static final Logger logger = LoggerFactory.getLogger(LeaveTypesServiceImpl.class);
	
	@Autowired
	LeaveTypeRepo leaveTypeRepo;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ModelMapper modelMapper;

	@Override
	public LeaveTypesDto addLeaveType(LeaveTypesDto leaveTypesDto) throws Exception{
		
//		LeaveType leaveType = null;
//		if(leaveTypesDto.getLeaveType().equalsIgnoreCase("sick_leave"))
//		{
//			leaveType = leaveType.SICK_LEAVE;
//		}
//		else if(leaveTypesDto.getLeaveType().equalsIgnoreCase("casual_leave"))
//		{
//			leaveType = leaveType.CASUAL_LEAVE;
//		}
//		else if(leaveTypesDto.getLeaveType().equalsIgnoreCase("leave_without_pay"))
//		{
//			leaveType = leaveType.LEAVE_WITHOUT_PAY;
//		}
//		else if(leaveTypesDto.getLeaveType().equalsIgnoreCase("earned_leave"))
//		{
//			leaveType = LeaveType.EARNED_LEAVE;
//		}
//		else
//		{
//			leaveType = leaveType.PATERNITY_LEAVE;
//		}
	try {
		logger.info("Creating LeaveType: {}", leaveTypesDto.getLeaveType() );
        
        if(leaveTypeRepo.findByLeaveType(leaveTypesDto.getLeaveType())!=null) {
    		throw new APIException("Leave Type "+leaveTypesDto.getLeaveType()+"  already exist");
    	}
//		
		LeaveTypes leaveTypes = new LeaveTypes();
		
		leaveTypes.setLeaveType(leaveTypesDto.getLeaveType());
		
		leaveTypes.setMaximumLeaves(leaveTypesDto.getMaximumLeaves());
		
		LeaveTypes savedLeaveType = leaveTypeRepo.save(leaveTypes);
		
		logger.info("Leave Type "+ leaveTypesDto.getLeaveType() + " created");
		
		return modelMapper.map(savedLeaveType,LeaveTypesDto.class);
		}catch(Exception e)
		{
            logger.error("Exception during creating leave", e);
            throw new APIException(e.getMessage());
		}
	}

	@Override
	public LeaveTypesDto updateLeaveType(LeaveTypesDto leaveTypesDto) {
		
		LeaveTypes leaveTypes = leaveTypeRepo.findByLeaveType(leaveTypesDto.getLeaveType());
		leaveTypes.setMaximumLeaves(leaveTypesDto.getMaximumLeaves());
		
		//to update remaining leaves count as count is updated in leave types 
		List<Users> allEmployees = userRepository.findAllByRole(Role.EMPLOYEE);
		
		if(!allEmployees.isEmpty()) {
		
			for(Users employee : allEmployees)
			{
				if(leaveTypes.getLeaveType().equals(LeaveType.SICK_LEAVE))
				{
					employee.setRemainingSickLeaves(leaveTypes.getMaximumLeaves()-employee.getApprovedSickLeaves());
				}
				else if(leaveTypes.getLeaveType().equals(LeaveType.CASUAL_LEAVE))
				{
					employee.setRemainingCasualLeaves(leaveTypes.getMaximumLeaves()-employee.getApprovedCasualLeaves());
				}
				else if(leaveTypes.getLeaveType().equals(LeaveType.EARNED_LEAVE))
				{
					employee.setRemainingEarnedLeaves(leaveTypes.getMaximumLeaves()-employee.getApprovedEarnedLeaves());
				}
				else if(leaveTypes.getLeaveType().equals(LeaveType.LEAVE_WITHOUT_PAY))
				{
					employee.setRemainingWithoutPayLeaves(leaveTypes.getMaximumLeaves()-employee.getApprovedWithoutPayLeaves());
				}
				else if(leaveTypes.getLeaveType().equals(LeaveType.PATERNITY_LEAVE))
				{
					employee.setRemainingPaternityLeaves(leaveTypes.getMaximumLeaves()-employee.getApprovedPaternityLeaves());
				}
			}
		}
		
		LeaveTypes updatedLeaveType = leaveTypeRepo.save(leaveTypes);
		
		return modelMapper.map(updatedLeaveType,LeaveTypesDto.class);
		
	}
}
