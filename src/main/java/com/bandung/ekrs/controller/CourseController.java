package com.bandung.ekrs.controller;

import com.bandung.ekrs.dto.CourseInfoDTO;
import com.bandung.ekrs.dto.response.StudentDataResponse;
import com.bandung.ekrs.service.CourseService;
import com.bandung.ekrs.service.StudentDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final StudentDataService studentDataService;

    @GetMapping
    public ResponseEntity<List<CourseInfoDTO>> getAllCourses(Authentication authentication) {
        String username = authentication.getName();
        
        // Get student data using the existing getCurrentStudentData method
        StudentDataResponse studentData = studentDataService.getCurrentStudentData(username);
        
        if (studentData.getDepartmentId() == null) {
            throw new RuntimeException("Student is not assigned to any department");
        }
        
        List<CourseInfoDTO> courses = courseService.getAllCoursesForDepartment(studentData.getDepartmentId());
        return ResponseEntity.ok(courses);
    }
} 