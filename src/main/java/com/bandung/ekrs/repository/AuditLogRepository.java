package com.bandung.ekrs.repository;

import com.bandung.ekrs.dto.AuditSummaryDTO;
import com.bandung.ekrs.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByUserId(Integer userId);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByTableAffected(String tableAffected);
    List<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT al FROM AuditLog al " +
           "WHERE (:userId IS NULL OR al.user.id = :userId) " +
           "AND (:action IS NULL OR al.action = :action) " +
           "AND (:tableAffected IS NULL OR al.tableAffected = :tableAffected) " +
           "AND al.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY al.timestamp DESC")
    Page<AuditLog> findAuditLogsWithFilters(
            @Param("userId") Integer userId,
            @Param("action") String action,
            @Param("tableAffected") String tableAffected,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT new com.bandung.ekrs.dto.AuditSummaryDTO(" +
           "al.action, COUNT(al)) " +
           "FROM AuditLog al " +
           "WHERE al.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY al.action")
    List<AuditSummaryDTO> getAuditSummary(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
} 