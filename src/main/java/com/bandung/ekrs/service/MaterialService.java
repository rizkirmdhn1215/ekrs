package com.bandung.ekrs.service;

import com.bandung.ekrs.dto.response.MaterialResponse;
import com.bandung.ekrs.model.Material;
import com.bandung.ekrs.model.Course;
import com.bandung.ekrs.model.Account;
import com.bandung.ekrs.model.enums.MaterialType;
import com.bandung.ekrs.repository.MaterialRepository;
import com.bandung.ekrs.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final CourseRepository courseRepository;
    private final MinioService minioService;
    
    @Transactional
    public MaterialResponse uploadMaterial(
            Integer courseId,
            String title,
            String description,
            MultipartFile file,
            MaterialType fileType,
            Account uploader
    ) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // Upload to MinIO
        String fileUrl = minioService.uploadFile(file, filename, "materials");

        // Create material record
        Material material = Material.builder()
                .course(course)
                .title(title)
                .description(description)
                .fileUrl(fileUrl)
                .fileType(fileType)
                .fileSize(file.getSize())
                .uploadDate(LocalDateTime.now())
                .lastModified(LocalDateTime.now())
                .isDeleted(false)
                .uploadedBy(uploader)
                .build();

        Material savedMaterial = materialRepository.save(material);
        
        return convertToDTO(savedMaterial);
    }

    public List<MaterialResponse> getCourseMaterials(Integer courseId) {
        return materialRepository.findByCourseIdAndNotDeleted(courseId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public void deleteMaterial(Integer materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));
                
        material.setIsDeleted(true);
        materialRepository.save(material);
    }

    private MaterialResponse convertToDTO(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .title(material.getTitle())
                .description(material.getDescription())
                .fileUrl(material.getFileUrl())
                .fileType(material.getFileType())
                .fileSize(material.getFileSize())
                .uploadDate(material.getUploadDate())
                .uploaderName(material.getUploadedBy() != null ? 
                    material.getUploadedBy().getUsername() : null)
                .courseName(material.getCourse() != null ? 
                    material.getCourse().getCourseName() : null)
                .build();
    }
}