package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudentStudentId(Integer studentId);
    List<Enrollment> findByCourseId(Integer courseId);
    List<Enrollment> findBySemesterId(Integer semesterId);
    List<Enrollment> findByStudentStudentIdAndSemesterId(Integer studentId, Integer semesterId);
    boolean existsByStudentStudentIdAndCourseIdAndSemesterId(Integer studentId, Integer courseId, Integer semesterId);
} 