package com.memplas.parking.feature.facility.service;

import com.memplas.parking.feature.facility.dto.ParkingFacilityDto;
import com.memplas.parking.feature.facility.mapper.ParkingFacilityMapper;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import com.memplas.parking.feature.facility.repository.ParkingFacilityRepository;
import com.memplas.parking.feature.parkingspot.dto.FloorSpotTypeCounts;
import com.memplas.parking.feature.parkingspot.dto.NearbyFacilitiesDto;
import com.memplas.parking.feature.parkingspot.service.AvailabilityCacheService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ParkingFacilityService {
    private final ParkingFacilityRepository facilityRepository;

    private final ParkingFacilityMapper facilityMapper;

    private final AvailabilityCacheService availabilityCacheService;

    public ParkingFacilityService(
            ParkingFacilityRepository facilityRepository,
            ParkingFacilityMapper facilityMapper,
            AvailabilityCacheService availabilityCacheService) {
        this.facilityRepository = facilityRepository;
        this.facilityMapper = facilityMapper;
        this.availabilityCacheService = availabilityCacheService;
    }

    // Public: Anyone can search for nearby facilities
    @Transactional(readOnly = true)
    public NearbyFacilitiesDto findNearbyFacilities(
            BigDecimal latitude, BigDecimal longitude, Integer radiusMeters) {

        // Performance: Single optimized geospatial query with distance calculation
        double radiusKm = radiusMeters / 1000.0;
        List<ParkingFacility> facilities = facilityRepository.findNearbyFacilities(latitude, longitude, radiusKm);
        var availability = availabilityCacheService.getFacilityAvailability(facilities.stream().map(ParkingFacility::getId).toList());
        return new NearbyFacilitiesDto(facilities.stream().map(facilityMapper::toDto).toList(), availability);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY_MANAGER')")
    public ParkingFacilityDto createFacility(ParkingFacilityDto facilityDto) {
        ParkingFacility facility = facilityMapper.toEntity(facilityDto);
        ParkingFacility savedFacility = facilityRepository.save(facility);
//No need to recalculate cache as when someone queries for availability of this facility cache will recalculate automatically
        return facilityMapper.toDto(savedFacility);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY_MANAGER')")
    public ParkingFacilityDto updateFacility(Long id, ParkingFacilityDto facilityDto) {
        ParkingFacility existingFacility = facilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Facility not found with id: " + id));

        ParkingFacility updatedFacility = facilityMapper.toEntity(facilityDto);
        updatedFacility.setId(id);
        updatedFacility.setCreatedAt(existingFacility.getCreatedAt());

        ParkingFacility savedFacility = facilityRepository.save(updatedFacility);

        return facilityMapper.toDto(savedFacility);
    }

    // Public: Anyone can view facility details
    @Transactional(readOnly = true)
    public ParkingFacilityDto getFacilityById(Long id) {
        ParkingFacility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Facility not found with id: " + id));
        return facilityMapper.toDto(facility);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFacility(Long id) {
        ParkingFacility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Facility not found with id: " + id));
        facilityRepository.delete(facility);
        availabilityCacheService.evictAllCache();
    }

    @Transactional(readOnly = true)
    public List<FloorSpotTypeCounts> getFloorSpotSummary(Long facilityId) {
        return availabilityCacheService.getFacilitySpotTypeCount(facilityId);
    }

}
