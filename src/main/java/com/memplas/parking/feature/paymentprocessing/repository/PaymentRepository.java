package com.memplas.parking.feature.paymentprocessing.repository;

import com.memplas.parking.feature.paymentprocessing.model.Payment;
import com.memplas.parking.feature.paymentprocessing.model.PaymentMethod;
import com.memplas.parking.feature.paymentprocessing.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByExternalPaymentId(String externalPaymentId);

    List<Payment> findBySessionId(Long sessionId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime createdBefore);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.processedAt >= :startDate AND p.processedAt < :endDate")
    BigDecimal getTotalRevenueInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.paymentMethod = :method")
    long countByStatusAndPaymentMethod(@Param("status") PaymentStatus status, @Param("method") PaymentMethod method);

    @Query("""
            SELECT p FROM Payment p 
            WHERE p.status = 'PENDING' 
            AND p.createdAt < :timeoutThreshold
            """)
    List<Payment> findTimedOutPayments(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);
}
