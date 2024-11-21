package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "course_prerequisites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursePrerequisite {
    @EmbeddedId
    private CoursePrerequisiteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("prerequisiteCourseId")
    @JoinColumn(name = "prerequisite_course_id")
    private Course prerequisiteCourse;

    @Column(name = "required_grade", length = 5)
    private String requiredGrade;

    @Column(name = "condition_type", length = 20)
    private String conditionType;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    private void validateConditionType() {
        if (conditionType != null) {
            String[] validTypes = {"Mandatory", "Recommended"};
            boolean isValid = Arrays.asList(validTypes).contains(conditionType);
            if (!isValid) {
                throw new IllegalArgumentException("Invalid condition type");
            }
        }
    }
} 