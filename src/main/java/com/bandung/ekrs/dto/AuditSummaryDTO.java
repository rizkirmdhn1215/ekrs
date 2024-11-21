package com.bandung.ekrs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditSummaryDTO {
    private String action;
    private Long count;
} 