package com.bandung.ekrs.service;

import com.bandung.ekrs.dto.response.*;
import com.bandung.ekrs.model.*;
import com.bandung.ekrs.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

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

        // Get current semester
        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Get enrollments for current student and semester
        List<EnrolledCourseResponse> enrolledCourses = enrollmentRepository
                .findByStudentStudentIdAndSemesterId(student.getStudentId(), currentSemester.getId())
                .stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    Integer currentEnrollment = enrollmentRepository
                            .countByCourseIdAndSemesterId(course.getId(), currentSemester.getId());
                    
                    return EnrolledCourseResponse.builder()
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
                            .maxStudents(course.getMaxStudents())
                            .currentEnrollment(currentEnrollment)
                            .build();
                })
                .toList();

        return EnrolledCoursesWrapper.builder()
                .data(enrolledCourses)
                .build();
    }

    public AvailableCoursesWrapper getAvailableCourses(String username) {
        // Get current student
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // Check if student has a department
        if (student.getDepartment() == null) {
            throw new RuntimeException("Student is not assigned to any department");
        }

        // Get current semester
        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Get all courses not enrolled by the student AND (in the same department OR with null department)
        List<Course> courses = courseRepository.findCoursesNotEnrolledByStudentAndDepartment(
            student.getStudentId(), 
            currentSemester.getId(),
            student.getDepartment().getId()
        );

        // Debug log
        System.out.println("Found " + courses.size() + " available courses");
        courses.forEach(course -> {
            System.out.println("Course: " + course.getCourseCode() + 
                             ", Department: " + (course.getDepartment() == null ? "NULL" : course.getDepartment().getId()));
        });

        List<AvailableCourseResponse> availableCourses = courses.stream()
                .map(course -> {
                    Integer currentEnrollment = enrollmentRepository
                            .countByCourseIdAndSemesterId(course.getId(), currentSemester.getId());
                    
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
                            .build();
                })
                .toList();

        return AvailableCoursesWrapper.builder()
                .data(availableCourses)
                .build();
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
        enrollment.setEnrollmentDate(LocalDate.now());

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

        // Delete the enrollment
        enrollmentRepository.delete(enrollment);

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
} 