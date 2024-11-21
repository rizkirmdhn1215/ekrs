package com.bandung.ekrs.dto.response;

import lombok.Getter;

@Getter
public enum EnrollmentStatusResponse {
    SUCCESS("T-ENR-SUCC-001", "Successfully enrolled in course", 200, "OK"),
    NOT_FOUND("T-ENR-ERR-001", "Course or student not found", 404, "NOT_FOUND"),
    ALREADY_ENROLLED("T-ENR-ERR-002", "Already enrolled in this course", 409, "CONFLICT"),
    INVALID_REQUEST("T-ENR-ERR-003", "Invalid enrollment request", 400, "BAD_REQUEST"),
    SERVER_ERROR("T-ENR-ERR-004", "Internal server error", 500, "INTERNAL_SERVER_ERROR");

    private final String code;
    private final String message;
    private final int statusCode;
    private final String status;

    EnrollmentStatusResponse(String code, String message, int statusCode, String status) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
        this.status = status;
    }
} 