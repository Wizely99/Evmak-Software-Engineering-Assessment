package com.memplas.parking.feature.facility.repository;

import com.memplas.parking.feature.facility.model.Floor;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long>, JpaSpecificationExecutor<Floor> {

    List<Floor> findByFacilityOrderByNumber(ParkingFacility facility);

    List<Floor> findByFacilityIdOrderByNumber(Long facilityId);

    boolean existsByFacilityIdAndNumber(Long facilityId, Integer number);
}