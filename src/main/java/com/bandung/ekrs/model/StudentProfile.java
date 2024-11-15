package com.bandung.ekrs.model;

import com.bandung.ekrs.model.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "student_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "student_number", nullable = false, unique = true)
    private String studentNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private LecturerProfile supervisor;

    @Column(name = "credit_limit")
    private Integer creditLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "student_status default 'PENDING'")
    private StudentStatus status;
} 