package com.bandung.ekrs.dto.student;

import lombok.Data;

@Data
public class StudentProfileDTO {
    private Integer student_id;
    private String namaSiswa;
    private String npm;
    private Integer angkatan;
    private Integer sks;
    private String dosenPembimbingAkademik;
    private String semester;
}