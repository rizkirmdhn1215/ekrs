package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.StudentBreak;
import com.bandung.ekrs.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentBreakRepository extends JpaRepository<StudentBreak, Integer> {
    List<StudentBreak> findByStudent(StudentProfile student);
    
    @Query("SELECT sb FROM StudentBreak sb WHERE sb.breakEnd >= :currentDate")
    List<StudentBreak> findActiveBreaks(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT sb FROM StudentBreak sb WHERE :date BETWEEN sb.breakStart AND sb.breakEnd")
    List<StudentBreak> findBreaksOnDate(@Param("date") LocalDate date);
    
    @Query("SELECT sb FROM StudentBreak sb WHERE sb.student = :student AND " +
           "((sb.breakStart BETWEEN :startDate AND :endDate) OR " +
           "(sb.breakEnd BETWEEN :startDate AND :endDate))")
    List<StudentBreak> findBreaksInPeriod(
        @Param("student") StudentProfile student,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT COUNT(sb) > 0 FROM StudentBreak sb WHERE sb.student = :student AND " +
           ":date BETWEEN sb.breakStart AND sb.breakEnd")
    boolean isStudentOnBreak(@Param("student") StudentProfile student, @Param("date") LocalDate date);
} 