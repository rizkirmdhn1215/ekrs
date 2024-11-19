package com.bandung.ekrs.service;


import com.bandung.ekrs.dto.editprofile.EditStudentProfileRequest;
import com.bandung.ekrs.dto.editprofile.EditStudentProfileResponse;
import com.bandung.ekrs.dto.editprofile.ProfileData;
import com.bandung.ekrs.model.Account;
import com.bandung.ekrs.model.Department;
import com.bandung.ekrs.model.StudentProfile;
import com.bandung.ekrs.model.enums.AccountRole;
import com.bandung.ekrs.model.enums.StudentStatus;
import com.bandung.ekrs.repository.AccountRepository;
import com.bandung.ekrs.repository.DepartmentRepository;
import com.bandung.ekrs.repository.StudentProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentProfileService {
    
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public EditStudentProfileResponse editProfile(EditStudentProfileRequest request) {
        try {
            // Get currently logged-in username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Find the account by username
            Account currentUser = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Akun tidak ditemukan"));

            // Verify that the user is a student
            if (currentUser.getRole() != AccountRole.STUDENT) {
                throw new RuntimeException("Hanya mahasiswa yang dapat mengedit profil");
            }

            // Find student profile by account
            StudentProfile studentProfile = studentProfileRepository.findByAccount(currentUser)
                .orElseThrow(() -> new RuntimeException("Profil mahasiswa tidak ditemukan"));

            // Find department by name (case insensitive)
            Department department = departmentRepository.findByNameIgnoreCase(request.getJurusan())
                .orElseThrow(() -> {
                    List<String> availableDepartments = departmentRepository.findAllDepartmentNames();
                    return new RuntimeException("Jurusan '" + request.getJurusan() + 
                        "' tidak ditemukan. Jurusan yang tersedia: " + String.join(", ", availableDepartments));
                });

            // Update fields from the request
            studentProfile.setFirstName(request.getFirstName());
            studentProfile.setLastName(request.getLastName());
            studentProfile.setDepartment(department);
            studentProfile.setAddress(request.getAlamat());

            // Save updated profile
            StudentProfile updatedProfile = studentProfileRepository.save(studentProfile);

            // Build successful response
            return EditStudentProfileResponse.builder()
                .data(ProfileData.builder()
                    .firstName(updatedProfile.getFirstName())
                    .lastName(updatedProfile.getLastName())
                    .jurusan(updatedProfile.getDepartment().getName())
                    .alamat(updatedProfile.getAddress())
                    .status(updatedProfile.getStatus())
                    .build())
                .message("Berhasil mengubah profil")
                .statusCode(200)
                .status("OK")
                .build();

        } catch (Exception e) {
            return EditStudentProfileResponse.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
        }
    }
}