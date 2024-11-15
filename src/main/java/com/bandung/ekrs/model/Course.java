package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "course_code", nullable = false, unique = true, length = 10)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "credit_points", nullable = false)
    private Integer creditPoints;

    @Column(name = "year_offered")
    private Integer yearOffered;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private LecturerProfile lecturer;

    @Column(name = "schedule_day")
    private String scheduleDay;

    @Column(name = "schedule_time")
    private LocalTime scheduleTime;

    @OneToMany(mappedBy = "course")
    private Set<CoursePrerequisite> prerequisites;

    @Column(length = 100)
    private String location;
} 