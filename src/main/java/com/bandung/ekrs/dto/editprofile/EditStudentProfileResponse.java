package com.bandung.ekrs.dto.editprofile;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditStudentProfileResponse {
    private ProfileData data;
    private String message;
    private int statusCode;
    private String status;

 
}