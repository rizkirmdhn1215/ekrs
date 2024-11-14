package com.bandung.ekrs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursePrerequisiteId implements Serializable {
    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "prerequisite_course_id")
    private Integer prerequisiteCourseId;
} 