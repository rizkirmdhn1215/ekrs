package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Account;
import com.bandung.ekrs.model.StudentProfile;
import com.bandung.ekrs.model.enums.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Integer> {
    Optional<StudentProfile> findByAccount(Account account);
    Optional<StudentProfile> findByNpm(String npm);
    
    // Add department-related queries
    List<StudentProfile> findByDepartmentId(Integer departmentId);
    
    @Query("SELECT sp FROM StudentProfile sp " +
           "WHERE sp.department.id = :departmentId " +
           "AND sp.status = :status")
    List<StudentProfile> findByDepartmentIdAndStatus(
            @Param("departmentId") Integer departmentId,
            @Param("status") StudentStatus status);
    
    // Optional: Count students by department
    @Query("SELECT COUNT(sp) FROM StudentProfile sp WHERE sp.department.id = :departmentId")
    Long countByDepartmentId(@Param("departmentId") Integer departmentId);
} 