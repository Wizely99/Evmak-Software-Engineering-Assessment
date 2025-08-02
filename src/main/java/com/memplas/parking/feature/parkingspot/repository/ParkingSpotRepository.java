package com.memplas.parking.feature.parkingspot.repository;

import com.memplas.parking.feature.parkingspot.dto.FacilitySpotCounts;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
import com.memplas.parking.feature.parkingspot.model.SpotStatus;
import com.memplas.parking.feature.parkingspot.model.SpotType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long>, JpaSpecificationExecutor<ParkingSpot> {
//    List<ParkingSpot> findByFacilityId(Long facilityId);
//
//    List<ParkingSpot> findByFacilityIdAndStatus(Long facilityId, SpotStatus status);
//
//    List<ParkingSpot> findByFacilityIdAndSpotType(Long facilityId, SpotType spotType);
//
//    Optional<ParkingSpot> findByFacilityIdAndSpotNumber(Long facilityId, String spotNumber);

    @Query("SELECT COUNT(ps) FROM ParkingSpot ps WHERE ps.facility.id = :facilityId AND ps.status = :status")
    int countByFacilityIdAndStatus(@Param("facilityId") Long facilityId, @Param("status") SpotStatus status);

    @Query("SELECT COUNT(ps) FROM ParkingSpot ps WHERE ps.facility.id = :facilityId AND ps.status = 'AVAILABLE'")
    int countAvailableSpots(@Param("facilityId") Long facilityId);

    @Query("""
            SELECT ps FROM ParkingSpot ps 
            WHERE ps.facility.id = :facilityId 
            AND ps.status = 'AVAILABLE'
            AND (:spotType IS NULL OR ps.spotType = :spotType)
            ORDER BY ps.spotNumber
            """)
    List<ParkingSpot> findAvailableSpots(
            @Param("facilityId") Long facilityId,
            @Param("spotType") SpotType spotType);

    @Modifying
    @Query("UPDATE ParkingSpot ps SET ps.status = 'AVAILABLE', ps.reservedBy = null, ps.reservationExpiry = null WHERE ps.reservationExpiry < :expiredTime")
    int releaseExpiredReservations(@Param("expiredTime") LocalDateTime expiredTime);

    @Query("SELECT ps FROM ParkingSpot ps WHERE ps.reservationExpiry < :expiredTime AND ps.status = 'RESERVED'")
    List<ParkingSpot> findExpiredReservations(@Param("expiredTime") LocalDateTime expiredTime);

    @Query("""
            SELECT new com.memplas.parking.feature.parkingspot.dto.FacilitySpotCounts(
                p.facility.id,
                COALESCE(SUM(CASE WHEN p.status = 'AVAILABLE' THEN 1 ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN p.status = 'OCCUPIED' THEN 1 ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN p.status = 'RESERVED' THEN 1 ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN p.status = 'OUT_OF_ORDER' THEN 1 ELSE 0 END), 0)
            )
            FROM ParkingSpot p 
            GROUP BY p.facility.id
            """)
    List<FacilitySpotCounts> getAllFacilitiesSpotCounts();
}
