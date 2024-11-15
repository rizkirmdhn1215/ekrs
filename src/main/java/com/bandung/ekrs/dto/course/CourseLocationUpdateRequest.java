package com.bandung.ekrs.dto.course;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseLocationUpdateRequest {
    private Integer courseId;
    private String location;
} 