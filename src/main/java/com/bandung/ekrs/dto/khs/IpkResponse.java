package com.bandung.ekrs.dto.khs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IpkResponse {
    private String student_id;
    private List<SemesterIpkDTO> semesterDetails;
    private double ipk;
    private int totalSksLulus;
    private String message;
    private int statusCode;
    private String status;
} 