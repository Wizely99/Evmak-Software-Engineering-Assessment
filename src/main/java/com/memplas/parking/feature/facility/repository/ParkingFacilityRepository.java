package com.memplas.parking.feature.facility.repository;

import com.memplas.parking.feature.facility.model.FacilityStatus;
import com.memplas.parking.feature.facility.model.FacilityType;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ParkingFacilityRepository extends JpaRepository<ParkingFacility, Long>, JpaSpecificationExecutor<ParkingFacility> {
    List<ParkingFacility> findByFacilityType(FacilityType facilityType);

    List<ParkingFacility> findByStatus(FacilityStatus status);

    List<ParkingFacility> findByFacilityTypeAndStatus(FacilityType facilityType, FacilityStatus status);

    @Query("""
            SELECT pf FROM ParkingFacility pf 
            WHERE pf.status = 'ACTIVE'
            AND (6371 * acos(cos(radians(:lat)) * cos(radians(pf.locationLat)) * 
            cos(radians(pf.locationLng) - radians(:lng)) + sin(radians(:lat)) * 
            sin(radians(pf.locationLat)))) <= :radiusKm
            ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(pf.locationLat)) * 
            cos(radians(pf.locationLng) - radians(:lng)) + sin(radians(:lat)) * 
            sin(radians(pf.locationLat))))
            """)
    List<ParkingFacility> findNearbyFacilities(
            @Param("lat") BigDecimal latitude,
            @Param("lng") BigDecimal longitude,
            @Param("radiusKm") Double radiusKm);

    @Query("SELECT SUM(pf.totalSpots) FROM ParkingFacility pf WHERE pf.status = 'ACTIVE'")
    Long getTotalActiveParkingSpots();
}
