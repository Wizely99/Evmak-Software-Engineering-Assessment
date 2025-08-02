package com.memplas.parking.feature.facility.service;

import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import com.memplas.parking.feature.facility.dto.FacilityAvailabilityDto;
import com.memplas.parking.feature.facility.dto.ParkingFacilityDto;
import com.memplas.parking.feature.facility.mapper.FacilityAvailabilityMapper;
import com.memplas.parking.feature.facility.mapper.ParkingFacilityMapper;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import com.memplas.parking.feature.facility.repository.FacilityAvailabilityCacheRepository;
import com.memplas.parking.feature.facility.repository.ParkingFacilityRepository;
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

    private final FacilityAvailabilityCacheRepository cacheRepository;

    private final ParkingFacilityMapper facilityMapper;

    private final FacilityAvailabilityMapper availabilityMapper;

    private final AvailabilityCacheService cacheService;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public ParkingFacilityService(
            ParkingFacilityRepository facilityRepository,
            FacilityAvailabilityCacheRepository cacheRepository,
            ParkingFacilityMapper facilityMapper,
            FacilityAvailabilityMapper availabilityMapper,
            AvailabilityCacheService cacheService,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.facilityRepository = facilityRepository;
        this.cacheRepository = cacheRepository;
        this.facilityMapper = facilityMapper;
        this.availabilityMapper = availabilityMapper;
        this.cacheService = cacheService;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    // Public: Anyone can search for nearby facilities
    @Transactional(readOnly = true)
    public List<FacilityAvailabilityDto> findNearbyFacilities(
            BigDecimal latitude, BigDecimal longitude, Integer radiusMeters) {

        // Performance: Single optimized geospatial query with distance calculation
        double radiusKm = radiusMeters / 1000.0;
        List<ParkingFacility> facilities = facilityRepository.findNearbyFacilities(latitude, longitude, radiusKm);

        // Performance: Batch load availability from cache to avoid N+1 queries
        return facilities.stream()
                .map(facility -> {
                    var availability = cacheService.getFacilityAvailability(facility.getId());
                    var dto = availabilityMapper.facilityToAvailabilityDto(facility);
                    // Merge availability data
                    return new FacilityAvailabilityDto(
                            dto.facilityId(), dto.facilityName(), dto.address(),
                            dto.locationLat(), dto.locationLng(),
                            availability.totalSpots(), availability.availableSpots(),
                            availability.occupiedSpots(), availability.reservedSpots(),
                            availability.outOfOrderSpots(), availability.currentOccupancyRate(),
                            dto.currentHourlyRate(), availability.lastUpdated()
                    );
                })
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY_MANAGER')")
    public ParkingFacilityDto createFacility(ParkingFacilityDto facilityDto) {
        ParkingFacility facility = facilityMapper.toEntity(facilityDto);
        ParkingFacility savedFacility = facilityRepository.save(facility);

        // Performance: Initialize cache entry immediately to avoid cold start
        cacheService.initializeFacilityCache(savedFacility.getId());

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
        cacheService.refreshFacilityCache(id);

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
    }
}
