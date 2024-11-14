package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
    List<Grade> findByEnrollmentId(Integer enrollmentId);
    List<Grade> findByEnrollmentStudentStudentId(Integer studentId);
    List<Grade> findByEnrollmentCourseId(Integer courseId);
} 