package com.bandung.ekrs.controller;

import com.bandung.ekrs.dto.request.EnrollCourseRequest;
import com.bandung.ekrs.dto.response.AvailableCoursesWrapper;
import com.bandung.ekrs.dto.response.EnrolledCoursesWrapper;
import com.bandung.ekrs.dto.response.EnrollmentResponse;
import com.bandung.ekrs.dto.response.StudentDataResponse;
import com.bandung.ekrs.dto.response.UnenrollmentResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @GetMapping("/enrolled")
    @Operation(
        summary = "Get enrolled courses",
        description = "Retrieves all courses that the current user is enrolled in"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved enrolled courses",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrolledCoursesWrapper.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Student profile not found",
            content = @Content
        )
    })
    public ResponseEntity<EnrolledCoursesWrapper> getEnrolledCourses(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.getEnrolledCourses(username));
    }

    @GetMapping("/available-courses")
    @Operation(
        summary = "Get available courses",
        description = "Retrieves all courses that the current user has not enrolled in for the current semester"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved available courses",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AvailableCoursesWrapper.class)
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
    public ResponseEntity<AvailableCoursesWrapper> getAvailableCourses(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.getAvailableCourses(username));
    }

    @PostMapping("/enroll")
    @Operation(
        summary = "Enroll in a course",
        description = "Enrolls the current user in a specified course"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully enrolled in the course",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or credit limit exceeded",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Course or student profile not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Already enrolled in this course",
            content = @Content
        )
    })
    public ResponseEntity<EnrollmentResponse> enrollCourse(
            Authentication authentication,
            @RequestBody EnrollCourseRequest request) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.enrollCourse(username, request.getCourseId()));
    }

    @DeleteMapping("/unenroll/{courseId}")
    @Operation(
        summary = "Unenroll from a course",
        description = "Removes the current user's enrollment from a specified course"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully unenrolled from the course",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UnenrollmentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Course enrollment not found",
            content = @Content
        )
    })
    public ResponseEntity<UnenrollmentResponse> unenrollCourse(
            Authentication authentication,
            @PathVariable Integer courseId) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.unenrollCourse(username, courseId));
    }

    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Update profile image",
        description = "Updates the profile image of the currently logged-in student"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully updated profile image"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid image file",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - User not authenticated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Student profile not found",
            content = @Content
        )
    })
    public ResponseEntity<String> updateProfileImage(
            Authentication authentication,
            @RequestParam("image") MultipartFile image) {
        try {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select an image file");
            }

            // Check file type
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("File must be an image");
            }

            // Check file size (e.g., 5MB limit)
            if (image.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File size must be less than 5MB");
            }

            studentDataService.updateProfileImage(authentication.getName(), image);
            return ResponseEntity.ok("Profile image updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update profile image: " + e.getMessage());
        }
    }
} 