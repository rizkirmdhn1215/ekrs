package com.bandung.ekrs.repository;

import com.bandung.ekrs.dto.AttendanceSummaryDTO;
import com.bandung.ekrs.model.Attendance;
import com.bandung.ekrs.model.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    List<Attendance> findByEnrollmentId(Integer enrollmentId);
    List<Attendance> findByEnrollmentStudentStudentId(Integer studentId);
    List<Attendance> findByEnrollmentCourseId(Integer courseId);
    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Attendance> findByStatus(AttendanceStatus status);

    @Query("SELECT a FROM Attendance a " +
           "LEFT JOIN FETCH a.enrollment e " +
           "LEFT JOIN FETCH e.student s " +
           "LEFT JOIN FETCH e.course c " +
           "WHERE e.student.studentId = :studentId " +
           "AND e.course.id = :courseId " +
           "AND a.date BETWEEN :startDate AND :endDate")
    List<Attendance> findDetailedAttendance(
            @Param("studentId") Integer studentId,
            @Param("courseId") Integer courseId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.bandung.ekrs.dto.AttendanceSummaryDTO(" +
           "a.status, COUNT(a)) " +
           "FROM Attendance a " +
           "WHERE a.enrollment.student.studentId = :studentId " +
           "AND a.enrollment.course.id = :courseId " +
           "GROUP BY a.status")
    List<AttendanceSummaryDTO> getAttendanceSummary(
            @Param("studentId") Integer studentId,
            @Param("courseId") Integer courseId);

    @Query("SELECT a FROM Attendance a " +
           "WHERE a.enrollment.student.studentId = :studentId " +
           "ORDER BY a.date DESC")
    Page<Attendance> findStudentAttendanceWithPagination(
            @Param("studentId") Integer studentId,
            Pageable pageable);
} 