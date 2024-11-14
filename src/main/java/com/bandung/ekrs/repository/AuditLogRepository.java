package com.bandung.ekrs.repository;

import com.bandung.ekrs.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByUserId(Integer userId);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByTableAffected(String tableAffected);
    List<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
} 