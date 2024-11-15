package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Course;
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
} 