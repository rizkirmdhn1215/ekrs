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
    List<Enrollment> findBySemesterId(Integer semesterId);
    List<Enrollment> findByStudentStudentIdAndSemesterId(Integer studentId, Integer semesterId);
    boolean existsByStudentStudentIdAndCourseIdAndSemesterId(Integer studentId, Integer courseId, Integer semesterId);
    Optional<Enrollment> findByStudentStudentIdAndCourseIdAndSemesterId(
        Integer studentId, 
        Integer courseId, 
        Integer semesterId
    );

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    Integer countByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.semester.id = :semesterId")
    Integer countByCourseIdAndSemesterId(
        @Param("courseId") Integer courseId,
        @Param("semesterId") Integer semesterId
    );

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.finished = false")
    Integer countActiveByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.semester.id = :semesterId AND e.finished = false")
    Integer countActiveByCourseIdAndSemesterId(
        @Param("courseId") Integer courseId,
        @Param("semesterId") Integer semesterId
    );

    @Query("SELECT e FROM Enrollment e WHERE e.student.studentId = :studentId AND e.semester.id = :semesterId AND e.finished = false")
    List<Enrollment> findActiveByStudentStudentIdAndSemesterId(
        @Param("studentId") Integer studentId,
        @Param("semesterId") Integer semesterId
    );

    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE " +
           "e.student.studentId = :studentId AND " +
           "e.course.id = :courseId AND " +
           "e.semester.id = :semesterId AND " +
           "e.finished = false")
    boolean hasActiveEnrollment(
        @Param("studentId") Integer studentId,
        @Param("courseId") Integer courseId,
        @Param("semesterId") Integer semesterId
    );

    @Query("SELECT e FROM Enrollment e " +
           "WHERE e.student.studentId = :studentId " +
           "AND e.course.id = :courseId")
    List<Enrollment> findByStudentStudentIdAndCourseId(
        @Param("studentId") Integer studentId,
        @Param("courseId") Integer courseId
    );

    @Query("SELECT e FROM Enrollment e " +
           "WHERE e.student.studentId = :studentId " +
           "AND e.finished = :finished")
    List<Enrollment> findByStudentStudentIdAndFinished(
        @Param("studentId") Integer studentId,
        @Param("finished") Boolean finished
    );

    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId")
    List<Enrollment> findByCourseId(@Param("courseId") Integer courseId);

} 