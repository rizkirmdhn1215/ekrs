package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.CourseFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface CourseFeedbackRepository extends JpaRepository<CourseFeedback, Integer> {
    List<CourseFeedback> findByCourseId(Integer courseId);
    List<CourseFeedback> findByStudentStudentId(Integer studentId);
    List<CourseFeedback> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
} 