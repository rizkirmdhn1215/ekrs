package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "student_breaks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentBreak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @Column(name = "break_start")
    private LocalDate breakStart;

    @Column(name = "break_end")
    private LocalDate breakEnd;

    @Column(name = "break_reason", columnDefinition = "text")
    private String breakReason;
} 