package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByLecturerLecturerId(Integer lecturerId);
    List<Course> findByYearOffered(Integer year);
    List<Course> findByScheduleDay(String day);
} 