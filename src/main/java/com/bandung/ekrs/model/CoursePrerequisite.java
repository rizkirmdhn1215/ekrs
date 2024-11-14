package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_prerequisites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursePrerequisite {
    @EmbeddedId
    private CoursePrerequisiteId id;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @MapsId("prerequisiteCourseId")
    @JoinColumn(name = "prerequisite_course_id")
    private Course prerequisiteCourse;

    @Column(name = "required_grade", length = 5)
    private String requiredGrade;

    @Column(name = "condition_type", length = 20)
    private String conditionType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 