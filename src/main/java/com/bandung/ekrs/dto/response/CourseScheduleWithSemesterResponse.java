package com.bandung.ekrs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseScheduleWithSemesterResponse {
    private Integer courseId;
    private String courseCode;
    private String courseName;
    private Integer creditPoints;
    private String scheduleDay;
    private String scheduleTime;
    private String location;
    private String lecturerName;
    private String departmentName;
    private Integer maxStudents;
    private Integer currentEnrolled;
    private SemesterResponse semester;
} 