package com.bandung.ekrs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyScheduleResponse {
    private List<CourseScheduleWithSemesterResponse> courses;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private String message;
    private int statusCode;
    private String status;
} 