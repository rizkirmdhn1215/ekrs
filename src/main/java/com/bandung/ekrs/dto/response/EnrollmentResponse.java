package com.bandung.ekrs.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private String message;
    private String courseCode;
    private String courseName;
    private Integer creditPoints;
    private Integer remainingCredits;
    private String lecturerName;
} 