package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Arrays;

@Entity
@Table(name = "courses", indexes = {
    @Index(name = "idx_courses_department_id", columnList = "department_id"),
    @Index(name = "idx_courses_semester_id", columnList = "semester_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "course_code", length = 10, nullable = false, unique = true)
    private String courseCode;

    @Column(name = "course_name", length = 100, nullable = false)
    private String courseName;

    @Column(name = "credit_points", nullable = false)
    private Integer creditPoints;

    @Column(name = "year_offered")
    private Integer yearOffered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private LecturerProfile lecturer;

    @Column(name = "schedule_day", length = 20)
    private String scheduleDay;

    @Column(name = "schedule_time")
    private LocalTime scheduleTime;

    @Column(name = "location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents = 30;

    @PrePersist
    @PreUpdate
    private void validateScheduleDay() {
        if (scheduleDay != null) {
            String[] validDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            boolean isValid = Arrays.asList(validDays).contains(scheduleDay);
            if (!isValid) {
                throw new IllegalArgumentException("Invalid schedule day");
            }
        }
    }
} 