package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "announcement_text", nullable = false)
    private String announcementText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Account createdBy;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    private void validateTargetAudience() {
        if (targetAudience != null) {
            String[] validAudiences = {"all", "lecturer", "student"};
            boolean isValid = Arrays.asList(validAudiences).contains(targetAudience.toLowerCase());
            if (!isValid) {
                throw new IllegalArgumentException("Invalid target audience");
            }
        }
    }
} 