package com.bandung.ekrs.controller;

import com.bandung.ekrs.dto.editprofile.EditStudentProfileRequest;
import com.bandung.ekrs.dto.editprofile.EditStudentProfileResponse;
import com.bandung.ekrs.service.StudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mahasiswa/profile")
public class StudentProfileController {

    @Autowired
    private StudentProfileService studentProfileService;

    @PutMapping("/edit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EditStudentProfileResponse> editProfile(
            @RequestBody EditStudentProfileRequest request) {
        
        EditStudentProfileResponse response = studentProfileService.editProfile(request);
        
        if (response.getStatusCode() == 200) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response);
        }
    }
}