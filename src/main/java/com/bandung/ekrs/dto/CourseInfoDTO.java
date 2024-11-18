package com.bandung.ekrs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseInfoDTO {
    private String courseName;
    private String courseCode;
    private Integer creditPoints;
    private String lecturerName;
    private String scheduleDay;
    private LocalTime scheduleTime;
    private String location;
    private Integer maxStudents;
    private Integer enrolledStudents;
    private String departmentName;
    private Boolean isGeneralCourse;
} 