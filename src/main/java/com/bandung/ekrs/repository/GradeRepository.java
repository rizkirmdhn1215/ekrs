package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Course;
import com.bandung.ekrs.model.Grade;
import com.bandung.ekrs.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
    @Query("SELECT g FROM Grade g " +
           "WHERE g.enrollment IN (SELECT e FROM Enrollment e " +
           "WHERE e.student = :student AND e.course = :course) " +
           "ORDER BY g.id DESC")
    List<Grade> findByEnrollmentStudentAndEnrollmentCourse(
        @Param("student") StudentProfile student,
        @Param("course") Course course
    );

    @Query("SELECT g FROM Grade g " +
           "WHERE g.enrollment.student.studentId = :studentId " +
           "AND g.enrollment.course.id = :courseId " +
           "AND g.enrollment.finished = true " +
           "ORDER BY g.id DESC")
    List<Grade> findLatestGradeByStudentAndCourse(
        @Param("studentId") Integer studentId,
        @Param("courseId") Integer courseId
    );

    @Query("SELECT g FROM Grade g " +
           "WHERE g.enrollment.id = :enrollmentId " +
           "ORDER BY g.id DESC")
    Optional<Grade> findLatestGradeByEnrollmentId(@Param("enrollmentId") Integer enrollmentId);

    Optional<Grade> findTopByEnrollmentStudentAndEnrollmentCourseOrderByCompletionDateDesc(
        StudentProfile student, 
        Course course
    );
} 