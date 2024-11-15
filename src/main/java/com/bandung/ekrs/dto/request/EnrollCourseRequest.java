package com.bandung.ekrs.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollCourseRequest {
    @NotNull(message = "Course ID is required")
    private Integer courseId;
} 