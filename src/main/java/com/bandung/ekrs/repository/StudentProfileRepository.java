package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Account;
import com.bandung.ekrs.model.LecturerProfile;
import com.bandung.ekrs.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Integer> {
    Optional<StudentProfile> findByAccount(Account account);
    Optional<StudentProfile> findByStudentNumber(String studentNumber);
    
    // Supervisor-related queries
    List<StudentProfile> findBySupervisor(LecturerProfile supervisor);
    List<StudentProfile> findBySupervisorIsNull();
    
    // Credit limit queries
    List<StudentProfile> findByCreditLimitLessThan(Integer creditLimit);
    List<StudentProfile> findByCreditLimitIsNull();
    
    @Modifying
    @Query("UPDATE StudentProfile sp SET sp.creditLimit = :newLimit WHERE sp.id = :studentId")
    void updateCreditLimit(@Param("studentId") Integer studentId, @Param("newLimit") Integer newLimit);

    // Additional useful queries
    @Query("SELECT sp FROM StudentProfile sp WHERE sp.creditLimit <= :maxLimit")
    List<StudentProfile> findStudentsWithCreditLimitUpTo(@Param("maxLimit") Integer maxLimit);
    
    // Updated to use lecturerId field
    @Query("SELECT sp FROM StudentProfile sp WHERE sp.supervisor.lecturerId = :lecturerId AND sp.creditLimit < :creditLimit")
    List<StudentProfile> findBySupervisorAndCreditLimitLessThan(
        @Param("lecturerId") Integer lecturerId, 
        @Param("creditLimit") Integer creditLimit
    );

    // Additional query to find by supervisor's department
    @Query("SELECT sp FROM StudentProfile sp WHERE sp.supervisor.department.id = :departmentId")
    List<StudentProfile> findBySuperviserDepartment(@Param("departmentId") Integer departmentId);
} 