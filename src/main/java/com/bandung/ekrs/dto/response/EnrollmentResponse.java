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
    private String code;
    private String message;
    private int statusCode;
    private String status;
    private EnrollmentData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentData {
        private String courseCode;
        private String courseName;
        private Integer creditPoints;
        private String semester;
    }

    public static EnrollmentResponse fromStatus(EnrollmentStatusResponse statusResponse) {
        return EnrollmentResponse.builder()
                .code(statusResponse.getCode())
                .message(statusResponse.getMessage())
                .statusCode(statusResponse.getStatusCode())
                .status(statusResponse.getStatus())
                .build();
    }

    public static EnrollmentResponse fromStatus(EnrollmentStatusResponse statusResponse, EnrollmentData data) {
        return EnrollmentResponse.builder()
                .code(statusResponse.getCode())
                .message(statusResponse.getMessage())
                .statusCode(statusResponse.getStatusCode())
                .status(statusResponse.getStatus())
                .data(data)
                .build();
    }
} 