package com.example.leavetracking1.repository;

import com.example.leavetracking1.entity.leave_info_view;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaveInfoViewRepository extends JpaRepository<leave_info_view, Long> {
    
    @Query("SELECT l.user_id, "+
    		"l.name, " +
    		"l.leaves_applied," +
    		"l.leaves_pending, " +
            "l.leaves_approved, " +
            "l.leaves_rejected, " +
            "l.approved_earned_leaves, " +
            "l.approved_without_pay_leaves, " +
            "l.approved_sick_leaves, " +
            "l.approved_casual_leaves, " +
            "l.approved_paternity_leaves, " +
            "l.rejected_earned_leaves, " +
            "l.rejected_without_pay_leaves, " +
            "l.rejected_sick_leaves, " +
            "l.rejected_casual_leaves, " +
            "l.rejected_paternity_leaves,  " +
            "l.remaining_earned_leaves, " +
            "l.remaining_without_pay_leaves, " +
            "l.remaining_sick_leaves, " +
            "l.remaining_casual_leaves, " +
            "l.remaining_paternity_leaves " +
            "FROM leave_info_view l " +
            "WHERE l.manager_id = :managerId")
     List<Object[]> getLeaveSummaryByManagerId(@Param("managerId") Long managerId);
     
     
     @Query("SELECT l.name, SUM(l.leaves_applied), SUM(l.leaves_approved),SUM(l.leaves_rejected), SUM(leaves_pending) " +
             "FROM leave_info_view l " +
             "WHERE l.manager_id = :managerId " +
             "AND l.start_date >= :startDate AND l.end_date <= :endDate GROUP BY l.name")
      List<Object[]> findLeaveDetailsByManagerIdAndStartDateBetween(long managerId, LocalDate startDate, LocalDate endDate);
}
