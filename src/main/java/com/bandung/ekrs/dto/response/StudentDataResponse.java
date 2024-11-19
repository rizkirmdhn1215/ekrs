package com.bandung.ekrs.dto.response;

import com.bandung.ekrs.model.enums.StudentStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDataResponse {
    private String firstName;
    private String lastName;
    private String npm;
    private String enrollmentYear;
    private Integer creditLimit;
    private Integer creditLeft;
    private SupervisorResponse supervisor;
    private StudentStatus status;
    private String address;
    private String imageUrl;
    private Integer departmentId;
    private String departmentName;
    private Integer currentSemesterNumber;
    private SemesterResponse currentSemester;
}