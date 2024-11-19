package com.bandung.ekrs.dto.editprofile;

import com.bandung.ekrs.model.enums.StudentStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileData {
    private String firstName;
    private String lastName;
    private String jurusan;
    private String alamat;
    private StudentStatus status;
}
