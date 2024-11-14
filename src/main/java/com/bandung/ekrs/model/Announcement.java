package com.bandung.ekrs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "announcement_text", nullable = false)
    private String announcementText;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Account createdBy;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 