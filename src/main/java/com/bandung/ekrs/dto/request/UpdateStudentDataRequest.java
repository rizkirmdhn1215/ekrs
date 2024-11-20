package com.bandung.ekrs.dto.request;

import lombok.Data;

@Data
public class UpdateStudentDataRequest {
    private String firstName;
    private String lastName;
    private String npm;
    private String address;
    private Integer creditLimit;
} 