package com.bandung.ekrs.service;

import com.bandung.ekrs.dto.response.*;
import com.bandung.ekrs.model.*;
import com.bandung.ekrs.repository.AccountRepository;
import com.bandung.ekrs.repository.CourseRepository;
import com.bandung.ekrs.repository.EnrollmentRepository;
import com.bandung.ekrs.repository.SemesterRepository;
import com.bandung.ekrs.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentDataService {
    private final AccountRepository accountRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SemesterRepository semesterRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public StudentDataResponse getCurrentStudentData(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StudentProfile student = studentProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

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
                .imageUrl(student.getImageUrl())
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
                    return EnrolledCourseResponse.builder()
                            .courseCode(course.getCourseCode())
                            .courseName(course.getCourseName())
                            .creditPoints(course.getCreditPoints())
                            .lecturerId(course.getLecturer() != null ? course.getLecturer().getLecturerId() : null)
                            .scheduleTime(course.getScheduleTime())
                            .scheduleDay(course.getScheduleDay())
                            .location(course.getLocation())
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

        // Get current semester
        Semester currentSemester = semesterRepository
                .findByStartDateBeforeAndEndDateAfter(LocalDate.now(), LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Current semester not found"));

        // Get all courses not enrolled by the student
        List<AvailableCourseResponse> availableCourses = courseRepository
                .findCoursesNotEnrolledByStudent(student.getStudentId(), currentSemester.getId())
                .stream()
                .map(course -> AvailableCourseResponse.builder()
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
                        .build())
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

        // Create new enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(currentSemester);
        enrollment.setEnrollmentDate(LocalDate.now());

        enrollmentRepository.save(enrollment);

        // Calculate remaining credits
        int remainingCredits = student.getCreditLimit() - (currentEnrolledCredits + course.getCreditPoints());

        return EnrollmentResponse.builder()
                .message("Successfully enrolled in course")
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .creditPoints(course.getCreditPoints())
                .remainingCredits(remainingCredits)
                .build();
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
} 