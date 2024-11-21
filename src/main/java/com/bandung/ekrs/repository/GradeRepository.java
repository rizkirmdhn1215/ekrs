package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Course;
import com.bandung.ekrs.model.Grade;
import com.bandung.ekrs.model.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
    @Query("SELECT g FROM Grade g " +
           "LEFT JOIN FETCH g.enrollment e " +
           "LEFT JOIN FETCH e.course " +
           "LEFT JOIN FETCH e.student " +
           "WHERE e.student.studentId = :studentId " +
           "AND e.course.id = :courseId " +
           "AND e.finished = true " +
           "ORDER BY g.completionDate DESC")
    List<Grade> findStudentCourseGradesWithDetails(
            @Param("studentId") Integer studentId,
            @Param("courseId") Integer courseId);

    @Query("SELECT g FROM Grade g " +
           "LEFT JOIN FETCH g.enrollment e " +
           "WHERE e.student.studentId = :studentId " +
           "ORDER BY g.completionDate DESC")
    Page<Grade> findStudentGradesWithPagination(
            @Param("studentId") Integer studentId,
            Pageable pageable);

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

    @Query("SELECT g FROM Grade g " +
           "WHERE g.enrollment.student = :student " +
           "AND g.enrollment.course = :course " +
           "ORDER BY g.completionDate DESC")
    List<Grade> findByEnrollmentStudentAndEnrollmentCourse(
            @Param("student") StudentProfile student,
            @Param("course") Course course);

    Optional<Grade> findTopByEnrollmentStudentAndEnrollmentCourseOrderByCompletionDateDesc(
        StudentProfile student, 
        Course course
    );

    @Query("SELECT g FROM Grade g " +
           "LEFT JOIN FETCH g.enrollment e " +
           "LEFT JOIN FETCH e.student s " +
           "LEFT JOIN FETCH e.course c " +
           "WHERE s = :student " +
           "AND c = :course " +
           "ORDER BY g.completionDate DESC")
    List<Grade> findGradesByStudentAndCourseWithDetails(
            @Param("student") StudentProfile student,
            @Param("course") Course course);
} 