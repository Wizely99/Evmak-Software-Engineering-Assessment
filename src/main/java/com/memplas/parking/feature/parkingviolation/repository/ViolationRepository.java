package com.memplas.parking.feature.parkingviolation.repository;

import com.memplas.parking.feature.parkingviolation.model.Violation;
import com.memplas.parking.feature.parkingviolation.model.ViolationStatus;
import com.memplas.parking.feature.parkingviolation.model.ViolationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long>, JpaSpecificationExecutor<Violation> {
    List<Violation> findByVehicleId(Long vehicleId);

    List<Violation> findBySpotId(Long spotId);

    List<Violation> findBySessionId(Long sessionId);

    List<Violation> findByStatus(ViolationStatus status);

    List<Violation> findByViolationType(ViolationType violationType);

    List<Violation> findByVehicleIdAndStatus(Long vehicleId, ViolationStatus status);

    @Query("""
            SELECT v FROM Violation v 
            WHERE v.spot.facility.id = :facilityId 
            AND v.detectedAt >= :startDate 
            AND v.detectedAt < :endDate
            """)
    List<Violation> findByFacilityAndDateRange(
            @Param("facilityId") Long facilityId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(v.fineAmount) FROM Violation v WHERE v.status = 'PAID' AND v.resolvedAt >= :startDate AND v.resolvedAt < :endDate")
    BigDecimal getTotalFinesCollectedInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(v) FROM Violation v WHERE v.violationType = :type AND v.detectedAt >= :startDate")
    long countByViolationTypeAndDateAfter(@Param("type") ViolationType type, @Param("startDate") LocalDateTime startDate);
}
