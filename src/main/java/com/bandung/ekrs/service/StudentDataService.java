package com.bandung.ekrs.service;

import com.bandung.ekrs.dto.response.SemesterResponse;
import com.bandung.ekrs.dto.response.StudentDataResponse;
import com.bandung.ekrs.dto.response.SupervisorResponse;
import com.bandung.ekrs.model.Account;
import com.bandung.ekrs.model.LecturerProfile;
import com.bandung.ekrs.model.Semester;
import com.bandung.ekrs.model.StudentProfile;
import com.bandung.ekrs.repository.AccountRepository;
import com.bandung.ekrs.repository.SemesterRepository;
import com.bandung.ekrs.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StudentDataService {
    private final AccountRepository accountRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SemesterRepository semesterRepository;

    public StudentDataResponse getCurrentStudentData(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Build supervisor response if supervisor exists
        SupervisorResponse supervisorResponse = null;
        if (student.getSupervisor() != null) {
            LecturerProfile supervisor = student.getSupervisor();
            supervisorResponse = SupervisorResponse.builder()
                    .lecturerId(supervisor.getLecturerId())
                    .firstName(supervisor.getFirstName())
                    .lastName(supervisor.getLastName())
                    .departmentName(supervisor.getDepartment() != null ? 
                            supervisor.getDepartment().getName() : null)
                    .build();
        }

        return StudentDataResponse.builder()
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .npm(student.getNpm())
                .enrollmentYear(student.getEnrollmentYear().toString())
                .creditLimit(student.getCreditLimit())
                .supervisor(supervisorResponse)
                .status(student.getStatus())
                .address(student.getAddress())
                .imageUrl(student.getImageUrl())
                .currentSemester(SemesterResponse.builder()
                        .name(currentSemester.getName())
                        .startDate(currentSemester.getStartDate().toString())
                        .endDate(currentSemester.getEndDate().toString())
                        .build())
                .build();
    }

    public StudentProfile findByNpm(String npm) {
        return studentProfileRepository.findByNpm(npm)
                .orElseThrow(() -> new RuntimeException("Student not found with NPM: " + npm));
    }

    public StudentDataResponse getStudentDataByNpm(String npm) {
        StudentProfile student = studentProfileRepository.findByNpm(npm)
                .orElseThrow(() -> new RuntimeException("Student not found with NPM: " + npm));

        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Build supervisor response if supervisor exists
        SupervisorResponse supervisorResponse = null;
        if (student.getSupervisor() != null) {
            LecturerProfile supervisor = student.getSupervisor();
            supervisorResponse = SupervisorResponse.builder()
                    .lecturerId(supervisor.getLecturerId())
                    .firstName(supervisor.getFirstName())
                    .lastName(supervisor.getLastName())
                    .departmentName(supervisor.getDepartment() != null ? 
                            supervisor.getDepartment().getName() : null)
                    .build();
        }

        return StudentDataResponse.builder()
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .npm(student.getNpm())
                .enrollmentYear(student.getEnrollmentYear().toString())
                .creditLimit(student.getCreditLimit())
                .supervisor(supervisorResponse)
                .status(student.getStatus())
                .address(student.getAddress())
                .imageUrl(student.getImageUrl())
                .currentSemester(SemesterResponse.builder()
                        .name(currentSemester.getName())
                        .startDate(currentSemester.getStartDate().toString())
                        .endDate(currentSemester.getEndDate().toString())
                        .build())
                .build();
    }
} 