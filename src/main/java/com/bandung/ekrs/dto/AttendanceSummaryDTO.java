package com.bandung.ekrs.dto;

import com.bandung.ekrs.model.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryDTO {
    private AttendanceStatus status;
    private Long count;
} 