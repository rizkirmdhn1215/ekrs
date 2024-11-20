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
public class KhsDetailDTO {

        private String semester;
        private String kodematkul;
        private String namamatkul;
        private String sks;
        private String nilai;
        private String bobot;
        private String completionDate;
    } 

