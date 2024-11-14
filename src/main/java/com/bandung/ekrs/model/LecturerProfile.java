package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "lecturer_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerProfile {
    @Id
    @Column(name = "lecturer_id")
    private Integer lecturerId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "lecturer_id")
    private Account account;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "image_url")
    private String imageUrl;
} 