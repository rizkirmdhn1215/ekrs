package com.bandung.ekrs.controller;


import com.bandung.ekrs.dto.khs.KhsResponse;
import com.bandung.ekrs.service.KhsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/khs")
@RequiredArgsConstructor
public class KhsController {
    private final KhsService khsService;

    @GetMapping("/semester")
    public ResponseEntity<KhsResponse> getKhsBySemester(@RequestParam Integer semester) {
        KhsResponse response = khsService.getKhsBySemester(semester);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}