package com.bandung.ekrs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private int status;
    private String message;
    private String error;
    private String path;
    
    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
    }
} 