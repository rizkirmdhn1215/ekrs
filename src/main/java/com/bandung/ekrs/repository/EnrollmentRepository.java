package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
           "e.finished = false AND " +
           "e.isDeleted = false")
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

    List<Enrollment> findByStudentStudentIdAndIsDeletedFalse(Integer studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.studentId = :studentId AND e.semester.id = :semesterId AND e.isDeleted = false")
    List<Enrollment> findActiveNotDeletedByStudentStudentIdAndSemesterId(
        @Param("studentId") Integer studentId,
        @Param("semesterId") Integer semesterId
    );

    @Query("SELECT e FROM Enrollment e " +
           "LEFT JOIN FETCH e.course c " +
           "LEFT JOIN FETCH e.student s " +
           "LEFT JOIN FETCH e.semester sem " +
           "WHERE s.studentId = :studentId " +
           "AND e.isDeleted = false " +
           "AND sem.id = :semesterId")
    List<Enrollment> findActiveEnrollmentsWithDetails(
            @Param("studentId") Integer studentId,
            @Param("semesterId") Integer semesterId);

    @Query("SELECT COUNT(e) FROM Enrollment e " +
           "WHERE e.course.id = :courseId " +
           "AND e.semester.id = :semesterId " +
           "AND e.isDeleted = false")
    Long countActiveEnrollments(
            @Param("courseId") Integer courseId,
            @Param("semesterId") Integer semesterId);

    @Query("SELECT e FROM Enrollment e " +
           "LEFT JOIN FETCH e.course " +
           "LEFT JOIN FETCH e.student " +
           "WHERE e.student.studentId = :studentId " +
           "AND e.isDeleted = false " +
           "ORDER BY e.semester.id DESC")
    Page<Enrollment> findStudentEnrollmentsWithPagination(
            @Param("studentId") Integer studentId,
            Pageable pageable);

    List<Enrollment> findByStudentStudentIdAndSemesterIdAndIsDeletedFalse(Integer studentId, Integer semesterId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.studentId = :studentId AND e.semester.id = :semesterId AND e.finished = false AND e.isDeleted = false")
    List<Enrollment> findByStudentStudentIdAndSemesterIdAndFinishedFalseAndIsDeletedFalse(
        @Param("studentId") Integer studentId, 
        @Param("semesterId") Integer semesterId
    );

    @Query("SELECT e FROM Enrollment e WHERE e.student.studentId = :studentId")
    List<Enrollment> findAllByStudentId(@Param("studentId") Integer studentId);

    @Query("SELECT e FROM Enrollment e " +
           "WHERE e.student.studentId = :studentId " +
           "AND e.finished = false " +
           "AND e.isDeleted = false")
    List<Enrollment> findActiveEnrollmentsByStudentId(
        @Param("studentId") Integer studentId
    );

} 