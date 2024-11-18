package com.bandung.ekrs.service;

import com.bandung.ekrs.dto.CourseInfoDTO;
import com.bandung.ekrs.model.Course;
import com.bandung.ekrs.repository.CourseRepository;
import com.bandung.ekrs.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<CourseInfoDTO> getAllCoursesForDepartment(Integer departmentId) {
        // Get both department-specific courses and courses with null department
        List<Course> departmentCourses = courseRepository.findByDepartmentId(departmentId);
        List<Course> basicCourses = courseRepository.findByDepartmentIsNull();
        
        // Combine both lists
        List<Course> allCourses = new ArrayList<>();
        allCourses.addAll(departmentCourses);
        allCourses.addAll(basicCourses);
        
        return allCourses.stream()
                .map(this::mapToCourseInfoDTO)
                .collect(Collectors.toList());
    }

    private CourseInfoDTO mapToCourseInfoDTO(Course course) {
        Integer totalEnrolled = enrollmentRepository.countByCourseId(course.getId());

        return CourseInfoDTO.builder()
                .courseName(course.getCourseName())
                .courseCode(course.getCourseCode())
                .creditPoints(course.getCreditPoints())
                .lecturerName(course.getLecturer() != null ? 
                    course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName() : 
                    null)
                .scheduleDay(course.getScheduleDay())
                .scheduleTime(course.getScheduleTime())
                .location(course.getLocation())
                .maxStudents(course.getMaxStudents())
                .enrolledStudents(totalEnrolled)
                .departmentName(course.getDepartment() != null ? 
                    course.getDepartment().getName() : 
                    "General Course")
                .isGeneralCourse(course.getDepartment() == null)
                .build();
    }
} 