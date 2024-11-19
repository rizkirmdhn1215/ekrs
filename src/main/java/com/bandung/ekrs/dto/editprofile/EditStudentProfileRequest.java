package com.bandung.ekrs.dto.editprofile;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditStudentProfileRequest {
    private String firstName;
    private String lastName;
    private String jurusan;
    private String alamat;

    
}