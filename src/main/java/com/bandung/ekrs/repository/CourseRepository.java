package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByCreditPoints(Integer creditPoints);
    
    // Location-based operations
    List<Course> findByLocation(String location);
    List<Course> findByLocationIsNull();
    
    @Query("SELECT c FROM Course c WHERE LOWER(c.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> findByLocationContainingIgnoreCase(@Param("keyword") String keyword);
    
    @Modifying
    @Query("UPDATE Course c SET c.location = :location WHERE c.id = :courseId")
    void updateLocation(@Param("courseId") Integer courseId, @Param("location") String location);
    
    // Other queries
    List<Course> findByYearOffered(Integer yearOffered);
    List<Course> findByScheduleDay(String scheduleDay);
    
    // Changed from findByLecturerId to use the correct path through the lecturer entity
    @Query("SELECT c FROM Course c WHERE c.lecturer.lecturerId = :lecturerId")
    List<Course> findByLecturerId(@Param("lecturerId") Integer lecturerId);
    
    @Query("SELECT c FROM Course c WHERE c.id NOT IN " +
           "(SELECT e.course.id FROM Enrollment e WHERE e.student.studentId = :studentId AND e.semester.id = :semesterId)")
    List<Course> findCoursesNotEnrolledByStudent(@Param("studentId") Integer studentId, 
                                                @Param("semesterId") Integer semesterId);
    
    

    // Optional: Find courses by department and year
    List<Course> findByDepartmentIdAndYearOffered(Integer departmentId, Integer yearOffered);

    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN Enrollment e ON e.course = c " +
           "AND e.student.studentId = :studentId " +
           "AND e.semester.id = :semesterId " +
           "WHERE (c.department.id = :departmentId OR c.department IS NULL) " +
           "AND (e IS NULL OR e.finished = true) " +  // Show if no enrollment or if finished
           "ORDER BY c.courseCode")
    List<Course> findCoursesNotEnrolledByStudentAndDepartment(
            @Param("studentId") Integer studentId,
            @Param("semesterId") Integer semesterId,
            @Param("departmentId") Integer departmentId);

    List<Course> findByDepartmentIsNull();

    @Query("SELECT c FROM Course c " +
           "WHERE c.department.id = :departmentId " +
           "AND c.semester.id = :semesterId")
    List<Course> findByDepartmentAndSemester(
        @Param("departmentId") Integer departmentId,
        @Param("semesterId") Integer semesterId
    );

    @Query("SELECT c FROM Course c " +
           "WHERE c.department.id = :departmentId " +
           "ORDER BY c.semester.id")
    List<Course> findByDepartmentId(@Param("departmentId") Integer departmentId);

    @Query("SELECT c FROM Course c " +
           "WHERE c.department.id = :departmentId OR c.department IS NULL " +
           "ORDER BY c.semester.id, c.courseCode")
    List<Course> findByDepartmentIdOrGeneralCourses(@Param("departmentId") Integer departmentId);

    // Alternative method name if you prefer using method naming convention
    List<Course> findByDepartmentIdOrDepartmentIsNullOrderBySemesterIdAscCourseCodeAsc(Integer departmentId);

    // Add commonly used fetch joins to reduce N+1 queries
    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN FETCH c.department " +
           "LEFT JOIN FETCH c.semester " +
           "LEFT JOIN FETCH c.lecturer " +
           "WHERE c.courseCode = :courseCode")
    Optional<Course> findByCourseCodeWithDetails(@Param("courseCode") String courseCode);

    // Optimize course search with pagination
    @Query("SELECT c FROM Course c " +
           "WHERE (:departmentId IS NULL OR c.department.id = :departmentId) " +
           "AND (:semesterId IS NULL OR c.semester.id = :semesterId) " +
           "AND (:yearOffered IS NULL OR c.yearOffered = :yearOffered) " +
           "ORDER BY c.courseCode")
    Page<Course> findCoursesWithFilters(
            @Param("departmentId") Integer departmentId,
            @Param("semesterId") Integer semesterId,
            @Param("yearOffered") Integer yearOffered,
            Pageable pageable);

    // Optimize enrollment check query
    @Query("SELECT c FROM Course c " +
           "WHERE c.id NOT IN (SELECT e.course.id FROM Enrollment e " +
           "WHERE e.student.studentId = :studentId " +
           "AND e.semester.id = :semesterId " +
           "AND e.isDeleted = false)")
    Page<Course> findAvailableCoursesForStudent(
            @Param("studentId") Integer studentId,
            @Param("semesterId") Integer semesterId,
            Pageable pageable);

    @Query("SELECT c FROM Course c " +
           "WHERE (:departmentId IS NULL OR c.department.id = :departmentId) " +
           "AND (:semesterId IS NULL OR c.semester.id = :semesterId) " +
           "AND (:search IS NULL OR " +
           "    c.courseCode LIKE %:search% OR " +
           "    c.courseName LIKE %:search%) " +
           "AND (:scheduleDay IS NULL OR c.scheduleDay = :scheduleDay) " +
           "ORDER BY c.courseCode")
    Page<Course> findCoursesWithFilters(
            @Param("departmentId") Integer departmentId,
            @Param("semesterId") Integer semesterId,
            @Param("search") String search,
            @Param("scheduleDay") String scheduleDay,
            Pageable pageable);

    @Query(value = "SELECT * FROM courses c " +
           "WHERE (c.department_id = :departmentId OR c.department_id IS NULL) " +
           "AND (:search IS NULL OR " +
           "    c.course_code ILIKE CONCAT('%', CAST(:search AS text), '%') OR " +
           "    c.course_name ILIKE CONCAT('%', CAST(:search AS text), '%')) " +
           "ORDER BY c.semester_id",
           nativeQuery = true)
    Page<Course> findAvailableCoursesWithFilters(
        @Param("departmentId") Integer departmentId,
        @Param("search") String search,
        Pageable pageable
    );

    @Query(value = "SELECT * FROM courses c " +
           "WHERE (c.department_id = :departmentId OR c.department_id IS NULL) " +
           "AND c.semester_id = :semesterId " +
           "AND (:search IS NULL OR " +
           "    c.course_code ILIKE CONCAT('%', CAST(:search AS text), '%') OR " +
           "    c.course_name ILIKE CONCAT('%', CAST(:search AS text), '%')) " +
           "ORDER BY c.semester_id",
           nativeQuery = true)
    Page<Course> findAvailableCoursesWithFilters(
        @Param("departmentId") Integer departmentId,
        @Param("search") String search,
        @Param("semesterId") Integer semesterId,
        Pageable pageable
    );

    @Query(value = "SELECT * FROM courses c " +
           "WHERE (c.department_id = :departmentId OR c.department_id IS NULL) " +
           "AND (:semesterId IS NULL OR c.semester_id = :semesterId) " +
           "AND (:search IS NULL OR " +
           "    c.course_code ILIKE CONCAT('%', CAST(:search AS text), '%') OR " +
           "    c.course_name ILIKE CONCAT('%', CAST(:search AS text), '%')) " +
           "AND (:scheduleDay IS NULL OR c.schedule_day = :scheduleDay) " +
           "ORDER BY c.semester_id, c.schedule_day, c.schedule_time",
           nativeQuery = true)
    Page<Course> findCoursesWithFilters(
        @Param("departmentId") Integer departmentId,
        @Param("search") String search,
        @Param("scheduleDay") String scheduleDay,
        @Param("semesterId") Integer semesterId,
        Pageable pageable
    );

    @Query("SELECT c FROM Course c " +
           "WHERE c.department.id = :departmentId " +
           "AND (:search IS NULL OR " +
           "CAST(c.courseCode AS string) LIKE CONCAT('%', CAST(:search AS string), '%') OR " +
           "CAST(c.courseName AS string) LIKE CONCAT('%', CAST(:search AS string), '%')) " +
           "AND c.semester.id = :semesterId")
    List<Course> findAvailableCoursesWithFiltersNoPage(
        @Param("departmentId") Integer departmentId,
        @Param("search") String search,
        @Param("semesterId") Integer semesterId
    );

    @Query("SELECT c FROM Course c " +
           "WHERE c.department.id = :departmentId " +
           "AND (:search IS NULL OR " +
           "CAST(c.courseCode AS string) LIKE CONCAT('%', CAST(:search AS string), '%') OR " +
           "CAST(c.courseName AS string) LIKE CONCAT('%', CAST(:search AS string), '%'))")
    List<Course> findAvailableCoursesWithFiltersNoPage(
        @Param("departmentId") Integer departmentId,
        @Param("search") String search
    );
} 