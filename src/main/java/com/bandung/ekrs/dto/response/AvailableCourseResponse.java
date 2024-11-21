package com.bandung.ekrs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableCourseResponse {
    private Integer courseId;
    private String courseCode;
    private String courseName;
    private Integer creditPoints;
    private Integer lecturerId;
    private String lecturerName;
    private LocalTime scheduleTime;
    private String scheduleDay;
    private String location;
    private Integer departmentId;
    private String departmentName;
    private Integer maxStudents;
    private Integer currentEnrollment;
    private Boolean isFull;
    private Boolean isFinished;
    private String grade;
    private String bobot;
    private Boolean prerequisitesMet;
    private Integer semesterId;
    private String semesterName;
} 