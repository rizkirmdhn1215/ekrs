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
public class KhsResponse {
    private List<KhsDTO> data;
    private String message;
    private int statusCode;
    private String status;
}

