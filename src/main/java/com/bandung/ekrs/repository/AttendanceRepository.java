package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Attendance;
import com.bandung.ekrs.model.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
} 