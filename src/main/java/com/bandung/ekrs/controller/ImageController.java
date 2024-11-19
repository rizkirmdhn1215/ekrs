package com.bandung.ekrs.controller;

import com.bandung.ekrs.service.MinioService;
import com.bandung.ekrs.service.StudentDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    private final StudentDataService studentDataService;
    private final MinioService minioService;

    @PostMapping("/profile")
    public ResponseEntity<String> uploadProfileImage(
            Authentication authentication,
            @RequestParam("image") MultipartFile image) {
        studentDataService.updateProfileImage(authentication.getName(), image);
        return ResponseEntity.ok("Profile image updated successfully");
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String fileName) {
        InputStream inputStream = minioService.getImage(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Adjust based on actual image type
                .body(new InputStreamResource(inputStream));
    }
} 