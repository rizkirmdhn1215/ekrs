package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findByCreatedById(Integer createdById);
    List<Announcement> findByTargetAudience(String targetAudience);
    List<Announcement> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
} 