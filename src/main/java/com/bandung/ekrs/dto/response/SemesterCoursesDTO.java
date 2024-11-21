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
public class SemesterCoursesDTO {
    private Integer semesterId;
    private String semesterName;
    private List<AvailableCourseResponse> courses;
} 