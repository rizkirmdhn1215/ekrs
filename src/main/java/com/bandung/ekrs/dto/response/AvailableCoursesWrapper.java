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
public class AvailableCoursesWrapper {
    private List<SemesterCoursesDTO> semesterCourses;
    private String message;
    private int statusCode;
    private String status;
} 