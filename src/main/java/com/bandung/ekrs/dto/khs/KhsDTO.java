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
public class KhsDTO {
    private String student_id;
    private List<KhsDetailDTO> khs;
}


