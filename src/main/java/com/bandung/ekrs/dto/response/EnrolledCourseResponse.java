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
public class EnrolledCourseResponse {
    private Integer courseId;
    private String courseCode;
    private String courseName;
    private Integer creditPoints;
    private Integer lecturerId;
    private LocalTime scheduleTime;
    private String scheduleDay;
    private String location;
} 