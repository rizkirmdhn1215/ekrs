package com.bandung.ekrs.service;

import com.bandung.ekrs.dto.khs.KhsDTO;
import com.bandung.ekrs.dto.khs.KhsDetailDTO;
import com.bandung.ekrs.dto.khs.KhsResponse;
import com.bandung.ekrs.model.Grade;
import com.bandung.ekrs.repository.KhsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KhsService {
    private final KhsRepository khsRepository;

    public KhsResponse getKhsBySemester(String semester) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Grade> grades = khsRepository.findBySemesterAndUsername(semester, username);

            if (grades.isEmpty()) {
                return KhsResponse.builder()
                    .message("T-SDT-ERR-001")
                    .statusCode(404)
                    .status("NOT_FOUND")
                    .build();
            }

            List<KhsDetailDTO> khsDetails = grades.stream()
                .map(grade -> KhsDetailDTO.builder()
                    .semester(grade.getEnrollment().getSemester().getName())
                    .kodematkul(grade.getEnrollment().getCourse().getCourseCode())
                    .sks(grade.getEnrollment().getCourse().getCreditPoints().toString())
                    .nilai(grade.getGrade())
                    .bobot(convertGradeToBobot(grade.getGrade()))
                    .build())
                .collect(Collectors.toList());

            KhsDTO khsDTO = KhsDTO.builder()
                .student_id(username)
                .khs(khsDetails)
                .build();

            return KhsResponse.builder()
                .data(List.of(khsDTO))
                .message("T-SDT-SUCC-001")
                .statusCode(200)
                .status("OK")
                .build();

        } catch (Exception e) {
            return KhsResponse.builder()
                .message("T-SDT-ERR-001")
                .statusCode(500)
                .status("INTERNAL_SERVER_ERROR")
                .build();
        }
    }

    private String convertGradeToBobot(String grade) {
        return switch (grade) {
            case "A" -> "4.0";
            case "AB" -> "3.5";
            case "B" -> "3.0";
            case "BC" -> "2.5";
            case "C" -> "2.0";
            case "D" -> "1.0";
            case "E" -> "0.0";
            default -> "0.0";
        };
    }
}
