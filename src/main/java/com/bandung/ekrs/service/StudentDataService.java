package com.bandung.ekrs.service;

import com.bandung.ekrs.dto.khs.*;
import com.bandung.ekrs.dto.request.UpdateStudentDataRequest;
import com.bandung.ekrs.dto.response.*;
import com.bandung.ekrs.model.*;
import com.bandung.ekrs.model.enums.AccountRole;
import com.bandung.ekrs.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentDataService {
    private final AccountRepository accountRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SemesterRepository semesterRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final CoursePrerequisiteRepository coursePrerequisiteRepository;
    private final GradeRepository gradeRepository;
    private final MinioService minioService;
    private final PasswordEncoder passwordEncoder;
    private final KhsRepository khsRepository;

    // Add a constant for default image
    private static final String DEFAULT_IMAGE = "sukvi.jpeg";

    private static final Map<String, String> GRADE_BOBOT = new HashMap<>() {{
        put("A", "4.0");
        put("AB", "3.5");
        put("B", "3.0");
        put("BC", "2.5");
        put("C", "2.0");
        put("D", "1.0");
        put("E", "0.0");
    }};

    public StudentDataResponse getCurrentStudentData(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        String imageUrl = getStudentImageUrl(student);
        int currentSemester = calculateCurrentSemester(student.getEnrollmentYear());
        Semester currentSemesterObj = getCurrentSemester();
        
        List<Enrollment> currentEnrollments = getCurrentEnrollments(
            student.getStudentId(), 
            currentSemesterObj.getId()
        );
        
        int totalEnrolledCredits = calculateTotalEnrolledCredits(currentEnrollments);
        int creditLeft = Math.max(0, student.getCreditLimit() - totalEnrolledCredits);

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
                .creditLeft(creditLeft)
                .supervisor(supervisorResponse)
                .status(student.getStatus())
                .address(student.getAddress())
                .imageUrl(imageUrl)
                .departmentId(student.getDepartment() != null ? student.getDepartment().getId() : null)
                .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : null)
                .currentSemesterNumber(currentSemester)
                .currentSemester(SemesterResponse.builder()
                        .name("Semester " + currentSemester)
                        .startDate(currentSemesterObj.getStartDate().toString())
                        .endDate(currentSemesterObj.getEndDate().toString())
                        .build())
                .build();
    }

    private int calculateCurrentSemester(Integer enrollmentYear) {
        LocalDate now = LocalDate.now();
        LocalDate enrollmentDate = LocalDate.of(enrollmentYear, 1, 1);
        
        // Calculate years difference
        int yearsDiff = now.getYear() - enrollmentDate.getYear();
        
        // Calculate which semester in current year (1 or 2)
        int currentYearSemester = now.getMonthValue() <= 6 ? 1 : 2;
        
        // Calculate total semesters
        // Each year has 2 semesters, then add current year's semester
        int totalSemesters = (yearsDiff * 2) + currentYearSemester;
        
        return totalSemesters;
    }

    public StudentProfile findByNpm(String npm) {
        return studentProfileRepository.findByNpm(npm)
                .orElseThrow(() -> new RuntimeException("Student not found with NPM: " + npm));
    }

    public StudentDataResponse getStudentDataByNpm(String npm) {
        StudentProfile student = studentProfileRepository.findByNpm(npm)
                .orElseThrow(() -> new RuntimeException("Student not found with NPM: " + npm));

        // Get image URL (will return default image URL if no image exists)
        String imageUrl = getStudentImageUrl(student);

        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Calculate total enrolled credits for current semester
        int totalEnrolledCredits = enrollmentRepository
                .findByStudentStudentIdAndSemesterId(student.getStudentId(), currentSemester.getId())
                .stream()
                .mapToInt(enrollment -> enrollment.getCourse().getCreditPoints())
                .sum();

        // Calculate credits left (ensure it doesn't go below 0)
        int creditLeft = Math.max(0, student.getCreditLimit() - totalEnrolledCredits);

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
                .creditLeft(creditLeft)
                .supervisor(supervisorResponse)
                .status(student.getStatus())
                .address(student.getAddress())
                .imageUrl(imageUrl)
                .departmentId(student.getDepartment() != null ? student.getDepartment().getId() : null)
                .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : null)
                .currentSemester(SemesterResponse.builder()
                        .name(currentSemester.getName())
                        .startDate(currentSemester.getStartDate().toString())
                        .endDate(currentSemester.getEndDate().toString())
                        .build())
                .build();
    }

    public EnrolledCoursesWrapper getEnrolledCourses(String username) {
        // Get current student
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Get only unfinished enrollments
        List<EnrolledScheduleResponse> enrolledCourses = enrollmentRepository
                .findByStudentStudentIdAndFinished(student.getStudentId(), false)
                .stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    Integer currentEnrollment = enrollmentRepository
                            .countActiveByCourseIdAndSemesterId(
                                course.getId(), 
                                enrollment.getSemester().getId()  // Use the enrollment's semester
                            );

                    return EnrolledScheduleResponse.builder()
                            .courseId(course.getId())
                            .courseCode(course.getCourseCode())
                            .courseName(course.getCourseName())
                            .creditPoints(course.getCreditPoints())
                            .lecturerId(course.getLecturer() != null ? course.getLecturer().getLecturerId() : null)
                            .lecturerName(course.getLecturer() != null ? 
                                    course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName() : null)
                            .scheduleTime(course.getScheduleTime())
                            .scheduleDay(course.getScheduleDay())
                            .location(course.getLocation())
                            .departmentId(course.getDepartment() != null ? course.getDepartment().getId() : null)
                            .departmentName(course.getDepartment() != null ? course.getDepartment().getName() : "General Course")
                            .maxStudents(course.getMaxStudents())
                            .currentEnrollment(currentEnrollment)
                            .isFull(currentEnrollment >= course.getMaxStudents())
                            .build();
                })
                .toList();

        return EnrolledCoursesWrapper.builder()
                .data(enrolledCourses)
                .build();
    }

    public AvailableCoursesWrapper getAvailableCourses(
        String username, 
        String search, 
        int page, 
        int size,
        Integer semesterId
    ) {
        try {
            // Get current student and their department
            Account account = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            StudentProfile student = studentProfileRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Student profile not found"));

            Department studentDepartment = student.getDepartment();
            if (studentDepartment == null) {
                throw new RuntimeException("Student department not found");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Course> coursePage;
            
            if (semesterId != null) {
                coursePage = courseRepository.findAvailableCoursesWithFilters(
                    studentDepartment.getId(),
                    search,
                    semesterId,
                    pageable
                );
            } else {
                coursePage = courseRepository.findAvailableCoursesWithFilters(
                    studentDepartment.getId(),
                    search,
                    pageable
                );
            }

            List<AvailableCourseResponse> courses = coursePage.getContent().stream()
                .map(course -> {
                    List<CoursePrerequisite> prerequisites = 
                        coursePrerequisiteRepository.findByCourseId(course.getId());
                    boolean prerequisitesMet = checkPrerequisites(student, prerequisites);
                    Integer currentEnrollment = enrollmentRepository.countActiveByCourseId(course.getId());
                    boolean isFull = currentEnrollment >= course.getMaxStudents();
                    Optional<Grade> studentGrade = gradeRepository
                        .findTopByEnrollmentStudentAndEnrollmentCourseOrderByCompletionDateDesc(
                            student, course);

                    return AvailableCourseResponse.builder()
                        .courseId(course.getId())
                        .courseCode(course.getCourseCode())
                        .courseName(course.getCourseName())
                        .creditPoints(course.getCreditPoints())
                        .lecturerId(course.getLecturer() != null ? course.getLecturer().getLecturerId() : null)
                        .lecturerName(course.getLecturer() != null ? 
                            course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName() : null)
                        .scheduleTime(course.getScheduleTime())
                        .scheduleDay(course.getScheduleDay())
                        .location(course.getLocation())
                        .departmentId(course.getDepartment() != null ? course.getDepartment().getId() : null)
                        .departmentName(course.getDepartment() != null ? 
                            course.getDepartment().getName() : "General Course")
                        .maxStudents(course.getMaxStudents())
                        .currentEnrollment(currentEnrollment)
                        .isFull(isFull)
                        .isFinished(studentGrade.isPresent())
                        .grade(studentGrade.map(Grade::getGrade).orElse(null))
                        .bobot(studentGrade.map(grade -> convertGradeToBobot(grade.getGrade())).orElse(null))
                        .prerequisitesMet(prerequisitesMet)
                        .semesterId(course.getSemester().getId())
                        .semesterName(course.getSemester().getName())
                        .build();
                })
                .collect(Collectors.toList());

            // If semesterId is provided, group by semester
            if (semesterId != null) {
                Map<Integer, List<AvailableCourseResponse>> coursesBySemester = new TreeMap<>();
                coursesBySemester.put(semesterId, courses);
                
                List<SemesterCoursesDTO> semesterCourses = coursesBySemester.entrySet().stream()
                    .map(entry -> SemesterCoursesDTO.builder()
                        .semesterId(entry.getKey())
                        .semesterName("Semester " + entry.getKey())
                        .courses(entry.getValue())
                        .build())
                    .collect(Collectors.toList());

                return AvailableCoursesWrapper.builder()
                    .semesterCourses(semesterCourses)
                    .currentPage(coursePage.getNumber())
                    .totalPages(coursePage.getTotalPages())
                    .totalElements(coursePage.getTotalElements())
                    .message("Successfully retrieved available courses")
                    .statusCode(200)
                    .status("OK")
                    .build();
            } else {
                // If no semesterId, return flat list of courses
                return AvailableCoursesWrapper.builder()
                    .courses(courses)  // Add this field to AvailableCoursesWrapper
                    .currentPage(coursePage.getNumber())
                    .totalPages(coursePage.getTotalPages())
                    .totalElements(coursePage.getTotalElements())
                    .message("Successfully retrieved available courses")
                    .statusCode(200)
                    .status("OK")
                    .build();
            }

        } catch (Exception e) {
            return AvailableCoursesWrapper.builder()
                .message(e.getMessage())
                .statusCode(500)
                .status("INTERNAL_SERVER_ERROR")
                .build();
        }
    }

    // Helper method to check prerequisites
    private boolean checkPrerequisites(StudentProfile student, List<CoursePrerequisite> prerequisites) {
        if (prerequisites.isEmpty()) {
            return true;
        }

        for (CoursePrerequisite prerequisite : prerequisites) {
            // Check if student has completed this prerequisite course
            boolean hasCompleted = enrollmentRepository.findByStudentStudentIdAndCourseId(
                    student.getStudentId(), 
                    prerequisite.getPrerequisiteCourse().getId()
                ).stream()
                .anyMatch(enrollment -> enrollment.getFinished() && 
                         enrollment.getGrades().stream()
                             .anyMatch(grade -> isPassingGrade(grade.getGrade())));

            if (!hasCompleted) {
                return false;
            }
        }
        return true;
    }

    private boolean isPassingGrade(String grade) {
        return Arrays.asList("A", "AB", "B", "BC", "C").contains(grade);
    }

    private String convertGradeToBobot(String grade) {
        return GRADE_BOBOT.getOrDefault(grade, "0.0");
    }

    @Transactional
    public EnrollmentResponse enrollCourse(String username, Integer courseId) {
        try {
            // Get current student
            Account account = accountRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User not found: {}", username);
                        return new RuntimeException("User not found");
                    });

            StudentProfile student = studentProfileRepository.findByAccount(account)
                    .orElseThrow(() -> {
                        log.error("Student profile not found for user: {}", username);
                        return new RuntimeException("Student profile not found");
                    });

            // Get current semester
            Semester currentSemester = semesterRepository
                    .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                    .orElseThrow(() -> {
                        log.error("Current semester not found");
                        return new RuntimeException("Current semester not found");
                    });

            // Check if course exists
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> {
                        log.error("Course not found with ID: {}", courseId);
                        return new RuntimeException("Course not found");
                    });

            // Check if already enrolled
            if (enrollmentRepository.hasActiveEnrollment(
                    student.getStudentId(), courseId, currentSemester.getId())) {
                log.warn("Student {} already enrolled in course {}", username, courseId);
                return EnrollmentResponse.fromStatus(EnrollmentStatusResponse.ALREADY_ENROLLED);
            }

            // Create new enrollment
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setSemester(currentSemester);
            enrollment.setFinished(false);

            // Save enrollment
            enrollmentRepository.save(enrollment);
            log.info("Successfully enrolled student {} in course {}", username, courseId);

            // Create enrollment data
            EnrollmentResponse.EnrollmentData enrollmentData = EnrollmentResponse.EnrollmentData.builder()
                    .courseCode(course.getCourseCode())
                    .courseName(course.getCourseName())
                    .creditPoints(course.getCreditPoints())
                    .semester(currentSemester.getName())
                    .build();

            return EnrollmentResponse.fromStatus(EnrollmentStatusResponse.SUCCESS, enrollmentData);

        } catch (RuntimeException e) {
            log.error("Error during course enrollment: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return EnrollmentResponse.fromStatus(EnrollmentStatusResponse.NOT_FOUND);
            }
            return EnrollmentResponse.fromStatus(EnrollmentStatusResponse.INVALID_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error during course enrollment: {}", e.getMessage());
            return EnrollmentResponse.fromStatus(EnrollmentStatusResponse.SERVER_ERROR);
        }
    }

    private void checkPrerequisites(StudentProfile student, Course course) {
        List<CoursePrerequisite> prerequisites = coursePrerequisiteRepository.findByCourseId(course.getId());
        
        if (!prerequisites.isEmpty()) {
            for (CoursePrerequisite prerequisite : prerequisites) {
                // Find the student's grades for the prerequisite course
                List<Grade> grades = gradeRepository.findByEnrollmentStudentAndEnrollmentCourse(
                    student, 
                    prerequisite.getPrerequisiteCourse()
                );

                // Get the latest grade (first in the list since we ordered by ID DESC)
                Optional<Grade> latestGrade = grades.stream().findFirst();

                // If mandatory prerequisite
                if ("Mandatory".equals(prerequisite.getConditionType())) {
                    if (latestGrade.isEmpty()) {
                        throw new RuntimeException(String.format(
                            "Prerequisite course %s must be completed before enrolling in %s",
                            prerequisite.getPrerequisiteCourse().getCourseCode(),
                            course.getCourseCode()
                        ));
                    }

                    String studentGrade = latestGrade.get().getGrade();
                    String requiredGrade = prerequisite.getRequiredGrade();

                    if (!isGradeSufficient(studentGrade, requiredGrade)) {
                        throw new RuntimeException(String.format(
                            "Minimum grade of %s required in %s, but achieved grade was %s",
                            requiredGrade,
                            prerequisite.getPrerequisiteCourse().getCourseCode(),
                            studentGrade
                        ));
                    }
                }
            }
        }
    }

    private boolean isGradeSufficient(String achievedGrade, String requiredGrade) {
        Map<String, Integer> gradeValues = new HashMap<>();
        gradeValues.put("A", 5);
        gradeValues.put("B", 4);
        gradeValues.put("C", 3);
        gradeValues.put("D", 2);
        gradeValues.put("E", 1);
        gradeValues.put("F", 0);

        Integer achievedValue = gradeValues.get(achievedGrade.toUpperCase());
        Integer requiredValue = gradeValues.get(requiredGrade.toUpperCase());

        if (achievedValue == null || requiredValue == null) {
            throw new RuntimeException("Invalid grade format");
        }

        return achievedValue >= requiredValue;
    }

    @Transactional
    public UnenrollmentResponse unenrollCourse(String username, Integer courseId) {
        // Get current student
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Get current semester
        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Find the enrollment
        Enrollment enrollment = enrollmentRepository
                .findByStudentStudentIdAndCourseIdAndSemesterId(
                    student.getStudentId(), 
                    courseId, 
                    currentSemester.getId()
                )
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        Course course = enrollment.getCourse();

        // Calculate current enrolled credits before unenrollment
        int currentEnrolledCredits = enrollmentRepository
                .findByStudentStudentIdAndSemesterId(student.getStudentId(), currentSemester.getId())
                .stream()
                .mapToInt(e -> e.getCourse().getCreditPoints())
                .sum();

        // Instead of deleting, mark as finished
        enrollment.setFinished(true);
        enrollmentRepository.save(enrollment);

        // Calculate updated credit left
        int updatedCreditLeft = student.getCreditLimit() - (currentEnrolledCredits - course.getCreditPoints());

        return UnenrollmentResponse.builder()
                .message("Successfully unenrolled from course")
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .creditPoints(course.getCreditPoints())
                .updatedCreditLeft(updatedCreditLeft)
                .build();
    }

    @Transactional
    public void updateProfileImage(String username, MultipartFile image) {
        try {
            // Get current user's account
            Account account = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Get current user's student profile
            StudentProfile student = studentProfileRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Student profile not found"));

            // Generate unique filename
            String fileName = String.format("student_%d_%d_%s",
                    student.getStudentId(),
                    System.currentTimeMillis(),
                    image.getOriginalFilename());

            // Upload new image to MinIO
            minioService.uploadImage(image, fileName);

            // Delete old image if exists (but don't delete default image)
            if (student.getImageUrl() != null) {
                String oldFileName = extractFileNameFromUrl(student.getImageUrl());
                if (!oldFileName.equals("sukvi.jpeg")) {
                    minioService.deleteImage(oldFileName);
                }
            }

            // Update only the image URL using a custom query
            studentProfileRepository.updateImageUrl(student.getStudentId(), fileName);
        } catch (Exception e) {
            log.error("Error updating profile image", e);
            throw new RuntimeException("Failed to update profile image", e);
        }
    }

    private String extractFileNameFromUrl(String url) {
        // If the URL contains a query string, remove it
        String path = url.split("\\?")[0];
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Transactional
    public StudentDataResponse updateStudentData(String username, UpdateStudentDataRequest request) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Update only the fields that are present in the request
        if (request.getFirstName() != null) {
            studentProfileRepository.updateFirstName(student.getStudentId(), request.getFirstName());
        }
        if (request.getLastName() != null) {
            studentProfileRepository.updateLastName(student.getStudentId(), request.getLastName());
        }
        if (request.getNpm() != null) {
            studentProfileRepository.updateNpm(student.getStudentId(), request.getNpm());
        }
        if (request.getAddress() != null) {
            studentProfileRepository.updateAddress(student.getStudentId(), request.getAddress());
        }
        if (request.getCreditLimit() != null) {
            studentProfileRepository.updateCreditLimit(student.getStudentId(), request.getCreditLimit());
        }

        // Return updated data
        return getCurrentStudentData(username);
    }

    @Transactional
    public void updatePassword(String username, String oldPassword, String newPassword) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if old password matches
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters long");
        }

        // Update only the password field using specific query
        accountRepository.updatePassword(
            account.getId(), 
            passwordEncoder.encode(newPassword)
        );
    }

    public WeeklyScheduleResponse getWeeklySchedule(
            String username, 
            String search, 
            String scheduleDay,
            Integer semesterId,
            int page, 
            int size) {
        try {
            StudentProfile student = getStudentProfile(username);
            Pageable pageable = PageRequest.of(page, size);
            
            // Validate scheduleDay if provided
            if (scheduleDay != null) {
                String[] validDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                if (!Arrays.asList(validDays).contains(scheduleDay)) {
                    return WeeklyScheduleResponse.builder()
                        .message("Invalid schedule day")
                        .statusCode(400)
                        .status("BAD_REQUEST")
                        .build();
                }
            }

            Page<Course> coursePage = courseRepository.findCoursesWithFilters(
                student.getDepartment().getId(),
                search,
                scheduleDay,
                semesterId,
                pageable
            );

            List<CourseScheduleWithSemesterResponse> schedules = coursePage.getContent().stream()
                .map(course -> {
                    Long currentEnrolled = enrollmentRepository.countActiveEnrollments(
                        course.getId(), 
                        getCurrentSemester().getId()
                    );

                    // Handle nullable dates
                    String startDate = course.getSemester().getStartDate() != null ? 
                        course.getSemester().getStartDate().toString() : null;
                    String endDate = course.getSemester().getEndDate() != null ? 
                        course.getSemester().getEndDate().toString() : null;

                    return CourseScheduleWithSemesterResponse.builder()
                        .courseId(course.getId())
                        .courseCode(course.getCourseCode())
                        .courseName(course.getCourseName())
                        .creditPoints(course.getCreditPoints())
                        .scheduleDay(course.getScheduleDay())
                        .scheduleTime(course.getScheduleTime() != null ? 
                            course.getScheduleTime().toString() : null)
                        .location(course.getLocation())
                        .lecturerName(course.getLecturer() != null ? 
                            course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName() : null)
                        .departmentName(course.getDepartment() != null ? 
                            course.getDepartment().getName() : null)
                        .maxStudents(course.getMaxStudents())
                        .currentEnrolled(currentEnrolled.intValue())
                        .semester(SemesterResponse.builder()
                            .id(course.getSemester().getId())
                            .name(course.getSemester().getName())
                            .startDate(startDate)  // Use nullable startDate
                            .endDate(endDate)      // Use nullable endDate
                            .build())
                        .build();
                })
                .collect(Collectors.toList());

            return WeeklyScheduleResponse.builder()
                .courses(schedules)
                .currentPage(coursePage.getNumber())
                .totalPages(coursePage.getTotalPages())
                .totalElements(coursePage.getTotalElements())
                .message("Success")
                .statusCode(200)
                .status("OK")
                .build();

        } catch (Exception e) {
            return WeeklyScheduleResponse.builder()
                .message("Error: " + e.getMessage())
                .statusCode(500)
                .status("INTERNAL_SERVER_ERROR")
                .build();
        }
    }

    public EnrolledCoursesWrapper getEnrolledSchedule(String username) {
        // Get current student
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Get current semester
        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Get active enrollments and map to response
        List<EnrolledScheduleResponse> enrolledSchedule = enrollmentRepository
                .findByStudentStudentIdAndSemesterId(student.getStudentId(), currentSemester.getId())
                .stream()
                .filter(enrollment -> !enrollment.getFinished())
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    Integer currentEnrollment = enrollmentRepository
                            .countActiveByCourseIdAndSemesterId(course.getId(), currentSemester.getId());

                    return EnrolledScheduleResponse.builder()
                            .courseId(course.getId())
                            .courseCode(course.getCourseCode())
                            .courseName(course.getCourseName())
                            .creditPoints(course.getCreditPoints())
                            .lecturerId(course.getLecturer() != null ? course.getLecturer().getLecturerId() : null)
                            .lecturerName(course.getLecturer() != null ? 
                                    course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName() : null)
                            .scheduleTime(course.getScheduleTime())
                            .scheduleDay(course.getScheduleDay())
                            .location(course.getLocation())
                            .departmentId(course.getDepartment() != null ? course.getDepartment().getId() : null)
                            .departmentName(course.getDepartment() != null ? course.getDepartment().getName() : "General Course")
                            .maxStudents(course.getMaxStudents())
                            .currentEnrollment(currentEnrollment)
                            .isFull(currentEnrollment >= course.getMaxStudents())
                            .build();
                })
                .toList();

        return EnrolledCoursesWrapper.builder()
                .data(enrolledSchedule)
                .build();
    }

    public KhsResponse getKhsBySemester(String username, Integer semesterId) {
        try {
            List<Grade> grades = khsRepository.findAllByUsernameAndSemesterId(username, semesterId);

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
                    .namamatkul(grade.getEnrollment().getCourse().getCourseName())
                    .sks(grade.getEnrollment().getCourse().getCreditPoints().toString())
                    .nilai(grade.getGrade())
                    .bobot(convertGradeToBobot(grade.getGrade()))
                    .completionDate(grade.getCompletionDate() != null ? grade.getCompletionDate().toString() : null)
                    .build())
                .collect(Collectors.toList());

            // Calculate IPS (Semester GPA)
            double totalBobot = grades.stream()
                .mapToDouble(grade -> Double.parseDouble(convertGradeToBobot(grade.getGrade())) * grade.getEnrollment().getCourse().getCreditPoints())
                .sum();
            int totalSks = grades.stream()
                .mapToInt(grade -> grade.getEnrollment().getCourse().getCreditPoints())
                .sum();
            double ips = totalSks > 0 ? totalBobot / totalSks : 0.0;
            ips = Math.round(ips * 100.0) / 100.0; // Round to two decimal places

            // Calculate IPK (Cumulative GPA)
            List<Grade> allGrades = khsRepository.findAllByUsername(username);
            double totalBobotAll = allGrades.stream()
                .mapToDouble(grade -> Double.parseDouble(convertGradeToBobot(grade.getGrade())) * grade.getEnrollment().getCourse().getCreditPoints())
                .sum();
            int totalSksAll = allGrades.stream()
                .mapToInt(grade -> grade.getEnrollment().getCourse().getCreditPoints())
                .sum();
            double ipk = totalSksAll > 0 ? totalBobotAll / totalSksAll : 0.0;
            ipk = Math.round(ipk * 100.0) / 100.0; // Round to two decimal places

            // SKS Lulus for the specified semester requires a minimum grade of C
            int sksLulus = grades.stream()
                .filter(grade -> {
                    String gradeValue = grade.getGrade();
                    return gradeValue.equals("A") || gradeValue.equals("AB") || gradeValue.equals("B") || gradeValue.equals("BC") || gradeValue.equals("C");
                })
                .mapToInt(grade -> grade.getEnrollment().getCourse().getCreditPoints())
                .sum();

            // Calculate MAX SKS for next semester
            int maxSks = calculateMaxSks(ips);

            KhsDTO khsDTO = KhsDTO.builder()
                .student_id(username)
                .khs(khsDetails)
                .build();

            return KhsResponse.builder()
                .data(List.of(khsDTO))
                .message("T-SDT-SUCC-001")
                .statusCode(200)
                .status("OK")
                .ips(ips)
                .ipk(ipk)
                .sksLulus(sksLulus)
                .maxSks(maxSks)
                .build();

        } catch (Exception e) {
            return KhsResponse.builder()
                .message("T-SDT-ERR-001")
                .statusCode(500)
                .status("INTERNAL_SERVER_ERROR")
                .build();
        }
    }

    private int calculateMaxSks(double ips) {
        if (ips >= 3.0) {
            return 24;

        } else {
            return 21; // If IPS is below 3.0, set max SKS to 21
        }
    }

    public IpkResponse getIpk(String username) {
        try {
            // Get all grades for the student
            List<Grade> allGrades = khsRepository.findAllByUsername(username);

            if (allGrades.isEmpty()) {
                return IpkResponse.builder()
                    .message("T-SDT-ERR-001")
                    .statusCode(404)
                    .status("NOT_FOUND")
                    .build();
            }

            // Group grades by semester
            Map<Integer, List<Grade>> gradesBySemester = allGrades.stream()
                .collect(Collectors.groupingBy(
                    grade -> grade.getEnrollment().getSemester().getId()
                ));

            // Calculate details for each semester
            List<SemesterIpkDTO> semesterDetails = gradesBySemester.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<Grade> semesterGrades = entry.getValue();
                    
                    // Calculate IPS for this semester
                    double totalBobot = semesterGrades.stream()
                        .mapToDouble(grade -> Double.parseDouble(convertGradeToBobot(grade.getGrade())) 
                            * grade.getEnrollment().getCourse().getCreditPoints())
                        .sum();
                    
                    int totalSks = semesterGrades.stream()
                        .mapToInt(grade -> grade.getEnrollment().getCourse().getCreditPoints())
                        .sum();
                    
                    double ips = totalSks > 0 ? totalBobot / totalSks : 0.0;
                    ips = Math.round(ips * 100.0) / 100.0; // Round to two decimal places

                    // Calculate SKS Lulus for this semester
                    int sksLulus = semesterGrades.stream()
                        .filter(grade -> {
                            String gradeValue = grade.getGrade();
                            return gradeValue.equals("A") || gradeValue.equals("AB") || 
                                   gradeValue.equals("B") || gradeValue.equals("BC") || 
                                   gradeValue.equals("C");
                        })
                        .mapToInt(grade -> grade.getEnrollment().getCourse().getCreditPoints())
                        .sum();

                    return SemesterIpkDTO.builder()
                        .semester(semesterGrades.get(0).getEnrollment().getSemester().getName())
                        .ips(ips)
                        .sksLulus(sksLulus)
                        .totalSks(totalSks)
                        .build();
                })
                .collect(Collectors.toList());

            // Calculate overall IPK
            double totalBobotAll = allGrades.stream()
                .mapToDouble(grade -> Double.parseDouble(convertGradeToBobot(grade.getGrade())) 
                    * grade.getEnrollment().getCourse().getCreditPoints())
                .sum();
            
            int totalSksAll = allGrades.stream()
                .mapToInt(grade -> grade.getEnrollment().getCourse().getCreditPoints())
                .sum();
            
            double ipk = totalSksAll > 0 ? totalBobotAll / totalSksAll : 0.0;
            ipk = Math.round(ipk * 100.0) / 100.0; // Round to two decimal places

            // Calculate total SKS Lulus
            int totalSksLulus = allGrades.stream()
                .filter(grade -> {
                    String gradeValue = grade.getGrade();
                    return gradeValue.equals("A") || gradeValue.equals("AB") || 
                           gradeValue.equals("B") || gradeValue.equals("BC") || 
                           gradeValue.equals("C");
                })
                .mapToInt(grade -> grade.getEnrollment().getCourse().getCreditPoints())
                .sum();

            return IpkResponse.builder()
                .student_id(username)
                .semesterDetails(semesterDetails)
                .ipk(ipk)
                .totalSksLulus(totalSksLulus)
                .message("T-SDT-SUCC-001")
                .statusCode(200)
                .status("OK")
                .build();

        } catch (Exception e) {
            return IpkResponse.builder()
                .message("T-SDT-ERR-001")
                .statusCode(500)
                .status("INTERNAL_SERVER_ERROR")
                .build();
        }
    }

    // Refactor image URL handling into a separate method
    private String getStudentImageUrl(StudentProfile student) {
        if (student.getImageUrl() != null && !student.getImageUrl().isEmpty()) {
            return minioService.getImageUrl(extractFileNameFromUrl(student.getImageUrl()));
        }
        return minioService.getImageUrl(DEFAULT_IMAGE);
    }

    private List<Enrollment> getCurrentEnrollments(Integer studentId, Integer semesterId) {
        return enrollmentRepository
            .findActiveNotDeletedByStudentStudentIdAndSemesterId(studentId, semesterId);
    }

    private int calculateTotalEnrolledCredits(List<Enrollment> enrollments) {
        return enrollments.stream()
            .filter(e -> !e.getIsDeleted())
            .mapToInt(enrollment -> enrollment.getCourse().getCreditPoints())
            .sum();
    }

    private Semester getCurrentSemester() {
        return semesterRepository
            .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
            .orElseThrow(() -> new RuntimeException("Current semester not found"));
    }

    private StudentProfile getStudentProfile(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }
} 