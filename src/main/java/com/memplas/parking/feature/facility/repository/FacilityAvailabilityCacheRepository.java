package com.memplas.parking.feature.facility.repository;

import com.memplas.parking.feature.facility.model.FacilityAvailabilityCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FacilityAvailabilityCacheRepository extends JpaRepository<FacilityAvailabilityCache, Long>, JpaSpecificationExecutor<FacilityAvailabilityCache> {
    @Query("SELECT fac FROM FacilityAvailabilityCache fac WHERE fac.availableSpots > 0 ORDER BY fac.availableSpots DESC")
    List<FacilityAvailabilityCache> findFacilitiesWithAvailability();

    @Query("SELECT fac FROM FacilityAvailabilityCache fac WHERE fac.currentOccupancyRate >= :threshold")
    List<FacilityAvailabilityCache> findHighOccupancyFacilities(@Param("threshold") BigDecimal threshold);

    @Modifying
    @Query("""
            UPDATE FacilityAvailabilityCache fac 
            SET fac.availableSpots = :available, 
                fac.occupiedSpots = :occupied, 
                fac.reservedSpots = :reserved, 
                fac.outOfOrderSpots = :outOfOrder,
                fac.currentOccupancyRate = :occupancyRate,
                fac.lastUpdated = :lastUpdated
            WHERE fac.facilityId = :facilityId
            """)
    int updateFacilityAvailability(
            @Param("facilityId") Long facilityId,
            @Param("available") Integer availableSpots,
            @Param("occupied") Integer occupiedSpots,
            @Param("reserved") Integer reservedSpots,
            @Param("outOfOrder") Integer outOfOrderSpots,
            @Param("occupancyRate") BigDecimal occupancyRate,
            @Param("lastUpdated") LocalDateTime lastUpdated);

    @Query("SELECT fac FROM FacilityAvailabilityCache fac WHERE fac.lastUpdated < :threshold")
    List<FacilityAvailabilityCache> findStaleCache(@Param("threshold") LocalDateTime threshold);
}
