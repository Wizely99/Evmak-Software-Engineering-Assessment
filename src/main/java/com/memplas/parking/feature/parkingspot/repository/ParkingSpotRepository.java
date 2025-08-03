package com.memplas.parking.feature.parkingspot.repository;

import com.memplas.parking.feature.parkingspot.dto.FacilitySpotCounts;
import com.memplas.parking.feature.parkingspot.dto.FloorSpotTypeCounts;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
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

    @Query("""
             SELECT new com.memplas.parking.feature.parkingspot.dto.FloorSpotTypeCounts(
                 p.facility.id,
                 p.facility.wheelChairAccessible,
                 p.floor.id,
                 p.floor.name,
                 COALESCE(SUM(CASE WHEN p.spotType = 'REGULAR' THEN 1 ELSE 0 END), 0),
                 COALESCE(SUM(CASE WHEN p.spotType = 'DISABLED' THEN 1 ELSE 0 END), 0),
                 COALESCE(SUM(CASE WHEN p.spotType = 'EV_CHARGING' THEN 1 ELSE 0 END), 0),
                 COALESCE(SUM(CASE WHEN p.spotType = 'COMPACT' THEN 1 ELSE 0 END), 0),
                 COALESCE(SUM(CASE WHEN p.spotType = 'VIP' THEN 1 ELSE 0 END), 0)
             )
             FROM ParkingSpot p
             WHERE p.status = 'AVAILABLE'
            GROUP BY p.facility.id,p.facility.wheelChairAccessible, p.floor.id, p.floor.name
            """)
    List<FloorSpotTypeCounts> getAllFacilitiesSpotTypeCounts();
}
