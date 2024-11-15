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
    Optional<Course> findByCode(String code);
    List<Course> findByCredits(Integer credits);
    
    // New queries for location-based operations
    List<Course> findByLocation(String location);
    
    // Find courses by location containing keyword (case-insensitive)
    @Query("SELECT c FROM Course c WHERE LOWER(c.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> findByLocationContainingIgnoreCase(@Param("keyword") String keyword);
    
    // Find courses with no location assigned
    List<Course> findByLocationIsNull();
    
    // Update course location
    @Modifying
    @Query("UPDATE Course c SET c.location = :location WHERE c.id = :courseId")
    void updateLocation(@Param("courseId") Integer courseId, @Param("location") String location);
} 