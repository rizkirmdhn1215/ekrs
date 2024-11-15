package com.bandung.ekrs.controller;

import com.bandung.ekrs.dto.response.StudentDataResponse;
import com.bandung.ekrs.service.StudentDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/krs")
@RequiredArgsConstructor
@Tag(name = "Student Data", description = "Endpoints for managing student data")
public class StudentDataController {
    private final StudentDataService studentDataService;

    @Operation(
        summary = "Get current student data",
        description = "Retrieves the data of the currently logged-in student including personal information and current semester details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved student data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StudentDataResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Student profile or current semester not found",
            content = @Content
        )
    })
    @GetMapping("/datamahasiswa")
    public ResponseEntity<StudentDataResponse> getCurrentStudentData(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.getCurrentStudentData(username));
    }
} 