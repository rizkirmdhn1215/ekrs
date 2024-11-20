package com.bandung.ekrs.dto.request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
} 