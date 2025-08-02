package com.memplas.parking.feature.parkingsession.repository;

import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import com.memplas.parking.feature.parkingsession.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long>, JpaSpecificationExecutor<ParkingSession> {
    Optional<ParkingSession> findBySessionReference(String sessionReference);

    List<ParkingSession> findByUserId(Long userId);

    List<ParkingSession> findByUserIdAndStatus(Long userId, SessionStatus status);

    List<ParkingSession> findBySpotId(Long spotId);

    List<ParkingSession> findBySpotIdAndStatus(Long spotId, SessionStatus status);

    Optional<ParkingSession> findBySpotIdAndStatusIn(Long spotId, List<SessionStatus> statuses);

    List<ParkingSession> findByVehicleId(Long vehicleId);

    @Query("SELECT ps FROM ParkingSession ps WHERE ps.status = :status AND ps.startTime <= :currentTime")
    List<ParkingSession> findActiveSessionsToStart(@Param("status") SessionStatus status, @Param("currentTime") LocalDateTime currentTime);

    @Query("""
            SELECT ps FROM ParkingSession ps 
            WHERE ps.status = 'ACTIVE' 
            AND ps.endTime IS NOT NULL 
            AND ps.endTime < :currentTime
            """)
    List<ParkingSession> findOverdueActiveSessions(@Param("currentTime") LocalDateTime currentTime);

    @Query("""
            SELECT ps FROM ParkingSession ps 
            WHERE ps.spot.id IN (
                SELECT spot.id FROM ParkingSpot spot WHERE spot.facility.id = :facilityId
            ) AND ps.status = 'ACTIVE'
            """)
    List<ParkingSession> findActiveSessionsByFacility(@Param("facilityId") Long facilityId);

    @Query("""
            SELECT COUNT(ps) FROM ParkingSession ps 
            WHERE ps.createdAt >= :startDate 
            AND ps.createdAt < :endDate
            """)
    long countSessionsInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
