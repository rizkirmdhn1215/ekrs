package com.bandung.ekrs.dto.khs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterIpkDTO {
    private String semester;
    private double ips;
    private int sksLulus;
    private int totalSks;
} 