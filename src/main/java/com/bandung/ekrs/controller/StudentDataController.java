package com.bandung.ekrs.controller;

import com.bandung.ekrs.dto.khs.IpkResponse;
import com.bandung.ekrs.dto.khs.KhsResponse;
import com.bandung.ekrs.dto.request.EnrollCourseRequest;
import com.bandung.ekrs.dto.request.UpdateStudentDataRequest;
import com.bandung.ekrs.dto.request.UpdatePasswordRequest;
import com.bandung.ekrs.dto.response.AvailableCoursesWrapper;
import com.bandung.ekrs.dto.response.EnrolledCoursesWrapper;
import com.bandung.ekrs.dto.response.EnrollmentResponse;
import com.bandung.ekrs.dto.response.StudentDataResponse;
import com.bandung.ekrs.dto.response.UnenrollmentResponse;
import com.bandung.ekrs.dto.response.CourseScheduleResponse;
import com.bandung.ekrs.dto.response.WeeklyScheduleResponse;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Data Mahasiswa", description = "Endpoint untuk mengelola data mahasiswa")
public class StudentDataController {
    private final StudentDataService studentDataService;

    @GetMapping("/datamahasiswa")
    @Operation(
        summary = "Mendapatkan data mahasiswa",
        description = "Mengambil data mahasiswa yang sedang login termasuk informasi pribadi dan detail semester saat ini"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil mengambil data mahasiswa",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StudentDataResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Tidak terautentikasi - Pengguna belum login",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profil mahasiswa atau semester saat ini tidak ditemukan",
            content = @Content
        )
    })
    public ResponseEntity<StudentDataResponse> getDataMahasiswa(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.getCurrentStudentData(username));
    }

    @GetMapping("/krs/mata-kuliah-diambil")
    @Operation(
        summary = "Mendapatkan mata kuliah yang diambil",
        description = "Mengambil semua mata kuliah yang sedang diambil oleh mahasiswa"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil mengambil data mata kuliah",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrolledCoursesWrapper.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Tidak terautentikasi - Pengguna belum login",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profil mahasiswa tidak ditemukan",
            content = @Content
        )
    })
    public ResponseEntity<EnrolledCoursesWrapper> getMataKuliahDiambil(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.getEnrolledCourses(username));
    }

    @GetMapping("/krs/mata-kuliah-tersedia")
    @Operation(
        summary = "Mendapatkan mata kuliah tersedia",
        description = "Mengambil semua mata kuliah yang tersedia untuk semester ini dengan paginasi dan pencarian"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil mengambil mata kuliah tersedia",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AvailableCoursesWrapper.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Tidak terautentikasi"),
        @ApiResponse(responseCode = "404", description = "Data tidak ditemukan")
    })
    public ResponseEntity<AvailableCoursesWrapper> getMataKuliahTersedia(
            Authentication authentication,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer semesterId) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.getAvailableCourses(
            username, 
            search, 
            page, 
            size,
            semesterId
        ));
    }

    @PostMapping("/krs/ambil-mata-kuliah")
    @Operation(
        summary = "Ambil mata kuliah",
        description = "Mendaftarkan mahasiswa ke mata kuliah yang dipilih"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil mendaftar mata kuliah",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrollmentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Permintaan tidak valid atau batas SKS terlampaui",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Tidak terautentikasi - Pengguna belum login",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mata kuliah atau profil mahasiswa tidak ditemukan",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Sudah terdaftar di mata kuliah ini",
            content = @Content
        )
    })
    public ResponseEntity<EnrollmentResponse> ambilMataKuliah(
            Authentication authentication,
            @RequestBody EnrollCourseRequest request) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.enrollCourse(username, request.getCourseId()));
    }

    @DeleteMapping("/krs/batalkan-mata-kuliah/{courseId}")
    @Operation(
        summary = "Batalkan mata kuliah",
        description = "Membatalkan pendaftaran mata kuliah yang dipilih"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil membatalkan mata kuliah",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UnenrollmentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Tidak terautentikasi - Pengguna belum login",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pendaftaran mata kuliah tidak ditemukan",
            content = @Content
        )
    })
    public ResponseEntity<UnenrollmentResponse> batalkanMataKuliah(
            Authentication authentication,
            @PathVariable Integer courseId) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.unenrollCourse(username, courseId));
    }

    @PostMapping(value = "/profile/foto-profil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> perbaruiFotoProfil(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) { // Changed from "image" to "file" to match frontend
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Silakan pilih file foto");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("File harus berupa gambar");
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Ukuran file harus kurang dari 5MB");
            }

            studentDataService.updateProfileImage(authentication.getName(), file);
            return ResponseEntity.ok().body("{\"message\": \"Foto profil berhasil diperbarui\"}"); // Return JSON response
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Gagal memperbarui foto profil: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/profile/perbarui-profil")
    @Operation(
        summary = "Perbarui profil mahasiswa",
        description = "Memperbarui data profil mahasiswa yang dapat diubah"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil memperbarui profil mahasiswa",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StudentDataResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Data permintaan tidak valid",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Tidak terautentikasi - Pengguna belum login",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profil mahasiswa tidak ditemukan",
            content = @Content
        )
    })
    public ResponseEntity<StudentDataResponse> perbaruiProfil(
            Authentication authentication,
            @RequestBody UpdateStudentDataRequest request) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.updateStudentData(username, request));
    }

    @PutMapping("/profile/perbarui-password")
    @Operation(
        summary = "Perbarui kata sandi",
        description = "Memperbarui kata sandi pengguna setelah verifikasi kata sandi lama"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil memperbarui kata sandi"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Kata sandi tidak valid atau tidak memenuhi persyaratan",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Kata sandi saat ini salah",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Pengguna tidak ditemukan",
            content = @Content
        )
    })
    public ResponseEntity<String> perbaruiPassword(
            Authentication authentication,
            @RequestBody UpdatePasswordRequest request) {
        try {
            studentDataService.updatePassword(
                authentication.getName(),
                request.getOldPassword(),
                request.getNewPassword()
            );
            return ResponseEntity.ok("Kata sandi berhasil diperbarui");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/matakuliah/all")
    @Operation(
        summary = "Mendapatkan jadwal mingguan",
        description = "Mengambil semua jadwal mata kuliah untuk jurusan mahasiswa dengan paginasi, pencarian, dan filter semester"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil mengambil jadwal mingguan",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = WeeklyScheduleResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Semester saat ini tidak ditemukan",
            content = @Content
        )
    })
    public ResponseEntity<WeeklyScheduleResponse> getJadwalMingguan(
            Authentication authentication,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String scheduleDay,
            @RequestParam(required = false) Integer semesterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studentDataService.getWeeklySchedule(
            authentication.getName(), 
            search,
            scheduleDay,
            semesterId,
            page, 
            size
        ));
    }

    @GetMapping("/dashboard/jadwal")
    @Operation(
        summary = "Mendapatkan jadwal yang diambil",
        description = "Mengambil semua jadwal mata kuliah yang sedang diambil oleh mahasiswa"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil mengambil jadwal mata kuliah",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EnrolledCoursesWrapper.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Tidak terautentikasi - Pengguna belum login",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Profil mahasiswa atau semester tidak ditemukan",
            content = @Content
        )
    })
    public ResponseEntity<EnrolledCoursesWrapper> getJadwalDiambil(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(studentDataService.getEnrolledCourses(username));
    }

    @GetMapping("/khs/semester")
    @Operation(
        summary = "Mendapatkan KHS berdasarkan semester",
        description = "Mengambil KHS mahasiswa untuk semester tertentu"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil mengambil KHS",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = KhsResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "KHS tidak ditemukan",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Terjadi kesalahan pada server",
            content = @Content
        )
    })
    public ResponseEntity<KhsResponse> getKhsBySemester(Authentication authentication, @RequestParam Integer semester) {
        String username = authentication.getName();
        KhsResponse response = studentDataService.getKhsBySemester(username, semester);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/khs/ipk")
    @Operation(
        summary = "Mendapatkan IPK dari semua semester",
        description = "Mengambil IPK mahasiswa dari seluruh semester yang telah ditempuh"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Berhasil mengambil IPK",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = IpkResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Data nilai tidak ditemukan",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Terjadi kesalahan pada server",
            content = @Content
        )
    })
    public ResponseEntity<IpkResponse> getIpk(Authentication authentication) {
        String username = authentication.getName();
        IpkResponse response = studentDataService.getIpk(username);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
} 