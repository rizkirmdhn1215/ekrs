package com.bandung.ekrs.dto.student;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditLimitUpdateRequest {
    private Integer studentId;
    private Integer creditLimit;
} 