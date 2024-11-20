package com.bandung.ekrs.service;

import com.bandung.ekrs.dto.khs.KhsDTO;
import com.bandung.ekrs.dto.khs.KhsDetailDTO;
import com.bandung.ekrs.dto.khs.KhsResponse;
import com.bandung.ekrs.dto.request.UpdateStudentDataRequest;
import com.bandung.ekrs.dto.response.*;
import com.bandung.ekrs.model.*;
import com.bandung.ekrs.model.enums.AccountRole;
import com.bandung.ekrs.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public StudentDataResponse getCurrentStudentData(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Get image URL (will return default image URL if no image exists)
        String imageUrl;
        if (student.getImageUrl() != null && !student.getImageUrl().isEmpty()) {
            // Get presigned URL for existing image
            imageUrl = minioService.getImageUrl(extractFileNameFromUrl(student.getImageUrl()));
        } else {
            // Get presigned URL for default image
            imageUrl = minioService.getImageUrl("sukvi.jpeg");
        }

        // Calculate current semester based on enrollment year
        int currentSemester = calculateCurrentSemester(student.getEnrollmentYear());
        
        // Get current semester from repository
        Semester currentSemesterObj = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Calculate total enrolled credits for current semester
        int totalEnrolledCredits = enrollmentRepository
                .findByStudentStudentIdAndSemesterId(student.getStudentId(), currentSemesterObj.getId())
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
        String imageUrl;
        if (student.getImageUrl() != null && !student.getImageUrl().isEmpty()) {
            imageUrl = minioService.getImageUrl(extractFileNameFromUrl(student.getImageUrl()));
        } else {
            imageUrl = minioService.getImageUrl("sukvi.jpeg");
        }

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

    public AvailableCoursesWrapper getAvailableCourses(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        if (student.getDepartment() == null) {
            throw new RuntimeException("Student is not assigned to any department");
        }

        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        List<Course> courses = courseRepository.findCoursesNotEnrolledByStudentAndDepartment(
            student.getStudentId(), 
            currentSemester.getId(),
            student.getDepartment().getId()
        );

        log.debug("Found {} available courses", courses.size());

        List<AvailableCourseResponse> availableCourses = courses.stream()
                .map(course -> {
                    Integer currentEnrollment = enrollmentRepository
                            .countActiveByCourseIdAndSemesterId(course.getId(), currentSemester.getId());
                    
                    // Get the latest grade directly from Grade table
                    List<Grade> grades = gradeRepository.findLatestGradeByStudentAndCourse(
                        student.getStudentId(), 
                        course.getId()
                    );
                    
                    Boolean isFinished = !grades.isEmpty();
                    String grade = null;
                    String bobot = null;

                    if (isFinished && !grades.isEmpty()) {
                        Grade latestGrade = grades.get(0); // Get the most recent grade
                        grade = latestGrade.getGrade();
                        bobot = convertGradeToBobot(grade);
                    }

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
                            .departmentName(course.getDepartment() != null ? course.getDepartment().getName() : "General Course")
                            .maxStudents(course.getMaxStudents())
                            .currentEnrollment(currentEnrollment)
                            .isFull(currentEnrollment >= course.getMaxStudents())
                            .isFinished(isFinished)
                            .grade(grade)
                            .bobot(bobot)
                            .build();
                })
                .toList();

        return AvailableCoursesWrapper.builder()
                .data(availableCourses)
                .build();
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

    @Transactional
    public EnrollmentResponse enrollCourse(String username, Integer courseId) {
        // Get current student
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Get current semester
        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Get the course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check department access
        if (course.getDepartment() != null && 
            (student.getDepartment() == null || 
             !student.getDepartment().getId().equals(course.getDepartment().getId()))) {
            throw new RuntimeException("You cannot enroll in courses from other departments");
        }

        // Check prerequisites
        checkPrerequisites(student, course);

        // Check if already enrolled
        if (enrollmentRepository.existsByStudentStudentIdAndCourseIdAndSemesterId(
                student.getStudentId(), courseId, currentSemester.getId())) {
            throw new RuntimeException("Already enrolled in this course");
        }

        // Calculate current enrolled credits
        int currentEnrolledCredits = enrollmentRepository
                .findByStudentStudentIdAndSemesterId(student.getStudentId(), currentSemester.getId())
                .stream()
                .mapToInt(enrollment -> enrollment.getCourse().getCreditPoints())
                .sum();

        // Check if enrolling would exceed credit limit
        if (currentEnrolledCredits + course.getCreditPoints() > student.getCreditLimit()) {
            throw new RuntimeException("Enrolling in this course would exceed your credit limit");
        }

        // Add check for course capacity
        Integer currentEnrollment = enrollmentRepository
                .countByCourseIdAndSemesterId(courseId, currentSemester.getId());
        
        if (currentEnrollment >= course.getMaxStudents()) {
            throw new RuntimeException("Course has reached maximum capacity");
        }

        // Create new enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(currentSemester);
        enrollment.setFinished(false);
        enrollmentRepository.save(enrollment);

        // Calculate remaining credits
        int remainingCredits = student.getCreditLimit() - (currentEnrolledCredits + course.getCreditPoints());

        // Get lecturer name
        String lecturerName = course.getLecturer() != null ? 
                course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName() : 
                null;

        return EnrollmentResponse.builder()
                .message("Successfully enrolled in course")
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .creditPoints(course.getCreditPoints())
                .remainingCredits(remainingCredits)
                .lecturerName(lecturerName)
                .build();
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

    public WeeklyScheduleResponse getWeeklySchedule(String username, int page, int size) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        if (student.getDepartment() == null) {
            throw new RuntimeException("Student is not assigned to any department");
        }

        // Get current semester
        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Get all courses for department
        List<Course> departmentCourses = courseRepository.findAll().stream()
                .filter(course -> course.getDepartment() != null && 
                        course.getDepartment().getId().equals(student.getDepartment().getId()))
                .toList();

        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, departmentCourses.size());
        List<Course> paginatedCourses = start < departmentCourses.size() ? 
                departmentCourses.subList(start, end) : 
                new ArrayList<>();

        // Map courses to response
        List<CourseScheduleResponse> allSchedules = paginatedCourses.stream()
                .map(course -> {
                    // Get current enrollment for the course
                    Integer currentEnrollment = enrollmentRepository
                            .countByCourseIdAndSemesterId(course.getId(), currentSemester.getId());

                    // Get enrollment status and grade
                    List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
                    String status = "Belum Diambil";
                    String grade = null;
                    String bobot = null;
                    
                    if (!enrollments.isEmpty()) {
                        Optional<Enrollment> latestEnrollment = enrollments.stream()
                            .filter(e -> e.getStudent().getStudentId().equals(student.getStudentId()))
                            .max(Comparator.comparing(Enrollment::getId));
                        
                        if (latestEnrollment.isPresent()) {
                            Boolean finished = latestEnrollment.get().getFinished();
                            status = finished != null && finished ? "Selesai" : "Sedang Diambil";
                            
                            // If course is finished, get the grade
                            if (finished != null && finished) {
                                Optional<Grade> latestGrade = gradeRepository
                                    .findLatestGradeByEnrollmentId(latestEnrollment.get().getId());
                                
                                if (latestGrade.isPresent()) {
                                    grade = latestGrade.get().getGrade();
                                    bobot = convertGradeToBobot(grade);
                                }
                            }
                        }
                    }

                    // Build lecturer name
                    String lecturerName = course.getLecturer() != null ?
                            course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName() :
                            null;

                    return CourseScheduleResponse.builder()
                            .courseId(course.getId())
                            .courseCode(course.getCourseCode())
                            .courseName(course.getCourseName())
                            .creditPoints(course.getCreditPoints())
                            .lecturerName(lecturerName)
                            .scheduleTime(course.getScheduleTime())
                            .scheduleDay(course.getScheduleDay())
                            .location(course.getLocation())
                            .maxStudents(course.getMaxStudents())
                            .currentEnrollment(currentEnrollment)
                            .status(status)
                            .grade(grade)
                            .bobot(bobot)
                            .build();
                })
                .toList();

        return WeeklyScheduleResponse.builder()
                .schedule(allSchedules)
                .build();
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
} 