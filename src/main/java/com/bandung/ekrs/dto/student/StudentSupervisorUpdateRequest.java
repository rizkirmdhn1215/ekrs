package com.bandung.ekrs.dto.student;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSupervisorUpdateRequest {
    private Integer studentId;
    private Integer supervisorId;
} 