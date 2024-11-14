package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentProfile student;

    @Column(name = "feedback_text")
    private String feedbackText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 