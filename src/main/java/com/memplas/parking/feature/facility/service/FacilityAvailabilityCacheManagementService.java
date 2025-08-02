package com.memplas.parking.feature.facility.service;

import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import com.memplas.parking.feature.facility.dto.FacilityAvailabilityDto;
import com.memplas.parking.feature.facility.mapper.FacilityAvailabilityMapper;
import com.memplas.parking.feature.facility.model.FacilityAvailabilityCache;
import com.memplas.parking.feature.facility.repository.FacilityAvailabilityCacheRepository;
import com.memplas.parking.feature.parkingspot.service.AvailabilityCacheService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FacilityAvailabilityCacheManagementService {
    private final FacilityAvailabilityCacheRepository cacheRepository;

    private final FacilityAvailabilityMapper availabilityMapper;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final AvailabilityCacheService cacheService;

    public FacilityAvailabilityCacheManagementService(
            FacilityAvailabilityCacheRepository cacheRepository,
            FacilityAvailabilityMapper availabilityMapper,
            AuthenticatedUserProvider authenticatedUserProvider,
            AvailabilityCacheService cacheService) {
        this.cacheRepository = cacheRepository;
        this.availabilityMapper = availabilityMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.cacheService = cacheService;
    }

    // Public: Anyone can view availability cache
    @Transactional(readOnly = true)
    public FacilityAvailabilityDto getFacilityCacheById(Long facilityId) {
        FacilityAvailabilityCache cache = cacheRepository.findById(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("Facility cache not found for ID: " + facilityId));
        return availabilityMapper.toDto(cache);
    }

    @Transactional(readOnly = true)
    public List<FacilityAvailabilityDto> getAllFacilityCaches() {
        List<FacilityAvailabilityCache> caches = cacheRepository.findAll();
        return caches.stream().map(availabilityMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<FacilityAvailabilityDto> getFacilitiesWithAvailability() {
        List<FacilityAvailabilityCache> caches = cacheRepository.findFacilitiesWithAvailability();
        return caches.stream().map(availabilityMapper::toDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FACILITY_MANAGER')")
    public FacilityAvailabilityDto refreshFacilityCache(Long facilityId) {
        // Verify cache exists
        if (!cacheRepository.existsById(facilityId)) {
            throw new EntityNotFoundException("Facility cache not found for ID: " + facilityId);
        }

        // Trigger cache refresh
        cacheService.refreshFacilityCache(facilityId);

        // Return updated cache
        return getFacilityCacheById(facilityId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void refreshAllCaches() {
        List<FacilityAvailabilityCache> caches = cacheRepository.findAll();
        caches.forEach(cache -> cacheService.refreshFacilityCache(cache.getFacilityId()));
    }
}
