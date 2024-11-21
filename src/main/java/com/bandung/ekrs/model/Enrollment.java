package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.time.LocalDate;

@Entity
@Table(name = "enrollment", indexes = {
    @Index(name = "idx_enrollment_student_id", columnList = "student_id"),
    @Index(name = "idx_enrollment_course_id", columnList = "course_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentProfile student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "semester")
    private Semester semester;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "finished")
    private Boolean finished = false;

    @OneToMany(mappedBy = "enrollment")
    private Set<Grade> grades;

    @OneToMany(mappedBy = "enrollment")
    private Set<Attendance> attendances;

} 