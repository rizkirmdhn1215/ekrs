package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Account;
import com.bandung.ekrs.model.StudentProfile;
import com.bandung.ekrs.model.enums.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Modifying
    @Query("UPDATE StudentProfile sp SET sp.imageUrl = :imageUrl WHERE sp.studentId = :studentId")
    void updateImageUrl(@Param("studentId") Integer studentId, @Param("imageUrl") String imageUrl);

    @Modifying
    @Transactional
    @Query("UPDATE StudentProfile sp " +
           "SET sp.firstName = :firstName " +
           "WHERE sp.studentId = :studentId")
    int updateFirstName(@Param("studentId") Integer studentId, @Param("firstName") String firstName);

    @Modifying
    @Transactional
    @Query("UPDATE StudentProfile sp " +
           "SET sp.lastName = :lastName " +
           "WHERE sp.studentId = :studentId")
    int updateLastName(@Param("studentId") Integer studentId, @Param("lastName") String lastName);

    @Modifying
    @Transactional
    @Query("UPDATE StudentProfile sp " +
           "SET sp.npm = :npm " +
           "WHERE sp.studentId = :studentId")
    int updateNpm(@Param("studentId") Integer studentId, @Param("npm") String npm);

    @Modifying
    @Transactional
    @Query("UPDATE StudentProfile sp " +
           "SET sp.address = :address " +
           "WHERE sp.studentId = :studentId")
    int updateAddress(@Param("studentId") Integer studentId, @Param("address") String address);

    @Modifying
    @Transactional
    @Query("UPDATE StudentProfile sp " +
           "SET sp.creditLimit = :creditLimit " +
           "WHERE sp.studentId = :studentId")
    int updateCreditLimit(@Param("studentId") Integer studentId, @Param("creditLimit") Integer creditLimit);
} 