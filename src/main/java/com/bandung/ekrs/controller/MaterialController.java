package com.bandung.ekrs.controller;

import com.bandung.ekrs.dto.response.MaterialResponse;
import com.bandung.ekrs.model.Account;
import com.bandung.ekrs.model.Material;
import com.bandung.ekrs.model.enums.MaterialType;
import com.bandung.ekrs.repository.AccountRepository;
import com.bandung.ekrs.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
@Tag(name = "Course Materials", description = "Endpoints for managing course materials")
public class MaterialController {
    private final MaterialService materialService;
    private final AccountRepository accountRepository;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload course material", description = "Upload a new material for a course")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Material uploaded successfully",
            content = @Content(schema = @Schema(implementation = MaterialResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<MaterialResponse> uploadMaterial(
            @RequestParam("courseId") Integer courseId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") MaterialType fileType,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Account uploader = accountRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        MaterialResponse response = materialService.uploadMaterial(
            courseId, 
            title, 
            description, 
            file, 
            fileType, 
            uploader
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get course materials", description = "Get all materials for a specific course")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Materials retrieved successfully",
            content = @Content(schema = @Schema(implementation = MaterialResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<List<MaterialResponse>> getCourseMaterials(@PathVariable Integer courseId) {
        List<MaterialResponse> materials = materialService.getCourseMaterials(courseId);
        return ResponseEntity.ok(materials);
    }

    @DeleteMapping("/{materialId}")
    @Operation(summary = "Delete material", description = "Soft delete a course material")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Material deleted successfully"
        ),
        @ApiResponse(responseCode = "404", description = "Material not found")
    })
    public ResponseEntity<Map<String, String>> deleteMaterial(@PathVariable Integer materialId) {
        materialService.deleteMaterial(materialId);
        return ResponseEntity.ok(Map.of("message", "Material deleted successfully"));
    }
} 