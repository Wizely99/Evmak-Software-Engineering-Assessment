package com.memplas.parking.feature.auditlogging.repository;

import com.memplas.parking.feature.auditlogging.model.AuditAction;
import com.memplas.parking.feature.auditlogging.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {
    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    List<AuditLog> findByAction(AuditAction action);

    @Query("SELECT al FROM AuditLog al WHERE al.createdAt >= :startDate AND al.createdAt < :endDate ORDER BY al.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId AND al.createdAt >= :startDate ORDER BY al.createdAt DESC")
    List<AuditLog> findUserActivitySince(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.action = :action AND al.createdAt >= :startDate")
    long countByActionSince(@Param("action") AuditAction action, @Param("startDate") LocalDateTime startDate);
}
