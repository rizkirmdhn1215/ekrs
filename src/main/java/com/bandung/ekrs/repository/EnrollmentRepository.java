package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudentStudentId(Integer studentId);
    List<Enrollment> findByCourseId(Integer courseId);
    List<Enrollment> findBySemesterId(Integer semesterId);
    List<Enrollment> findByStudentStudentIdAndSemesterId(Integer studentId, Integer semesterId);
    boolean existsByStudentStudentIdAndCourseIdAndSemesterId(Integer studentId, Integer courseId, Integer semesterId);
    Optional<Enrollment> findByStudentStudentIdAndCourseIdAndSemesterId(
        Integer studentId, 
        Integer courseId, 
        Integer semesterId
    );

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.semester.id = :semesterId")
    Integer countByCourseIdAndSemesterId(@Param("courseId") Integer courseId, @Param("semesterId") Integer semesterId);
} 