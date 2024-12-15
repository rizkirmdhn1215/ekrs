package com.bandung.ekrs.dto.response;

import com.bandung.ekrs.model.enums.MaterialType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MaterialResponse {
    private Integer id;
    private String title;
    private String description;
    private String fileUrl;
    private MaterialType fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private String uploaderName;
    private String courseName;
} 