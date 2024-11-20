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
public class CourseScheduleResponse {
    private Integer courseId;
    private String courseCode;
    private String courseName;
    private Integer creditPoints;
    private String lecturerName;
    private LocalTime scheduleTime;
    private String scheduleDay;
    private String location;
    private Integer maxStudents;
    private Integer currentEnrollment;
    private String status;
    private String grade;
    private String bobot;
} 