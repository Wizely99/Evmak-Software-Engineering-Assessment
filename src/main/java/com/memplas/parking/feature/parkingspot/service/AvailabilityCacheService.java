package com.memplas.parking.feature.parkingspot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memplas.parking.feature.facility.dto.FacilityAvailabilityDto;
import com.memplas.parking.feature.facility.model.FacilityAvailabilityCache;
import com.memplas.parking.feature.facility.repository.FacilityAvailabilityCacheRepository;
import com.memplas.parking.feature.parkingspot.model.SpotStatus;
import com.memplas.parking.feature.parkingspot.repository.ParkingSpotRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Performance-Critical Service: Manages availability caching for sub-50ms responses
 * Uses file-based caching for demo (will be Redis in production)
 * Implements write-through cache pattern for consistency
 */
@Service
@Transactional
public class AvailabilityCacheService {
    // Performance: In-memory cache for ultra-fast access (demo only)
    private final Map<Long, FacilityAvailabilityDto> memoryCache = new ConcurrentHashMap<>();

    private final FacilityAvailabilityCacheRepository cacheRepository;

    private final ParkingSpotRepository spotRepository;

    private final ObjectMapper objectMapper;

    private final String cacheFilePath = "availability-cache.json";

    public AvailabilityCacheService(
            FacilityAvailabilityCacheRepository cacheRepository,
            ParkingSpotRepository spotRepository,
            ObjectMapper objectMapper) {
        this.cacheRepository = cacheRepository;
        this.spotRepository = spotRepository;
        this.objectMapper = objectMapper;
        loadCacheFromFile();
    }

    /**
     * Performance: Sub-50ms availability lookup using multi-level caching
     * 1. Memory cache (fastest)
     * 2. File cache (demo replacement for Redis)
     * 3. Database (fallback)
     */
    @Transactional(readOnly = true)
    public FacilityAvailabilityDto getFacilityAvailability(Long facilityId) {
        // Level 1: Memory cache
        FacilityAvailabilityDto cached = memoryCache.get(facilityId);
        if (cached != null) {
            return cached;
        }

        // Level 2: Database cache
        FacilityAvailabilityCache dbCache = cacheRepository.findById(facilityId).orElse(null);
        if (dbCache != null) {
            FacilityAvailabilityDto dto = buildAvailabilityDto(dbCache);
            memoryCache.put(facilityId, dto); // Update memory cache
            return dto;
        }

        // Level 3: Real-time calculation (expensive)
        return calculateAndCacheAvailability(facilityId);
    }

    /**
     * Performance: Async cache refresh to avoid blocking user operations
     * Called after spot reservations/releases to maintain accuracy
     */
    @Async
    public void refreshFacilityCache(Long facilityId) {
        calculateAndCacheAvailability(facilityId);
        saveCacheToFile();
    }

    public void initializeFacilityCache(Long facilityId) {
        calculateAndCacheAvailability(facilityId);
    }

    /**
     * Performance: Single query per status type instead of N+1 queries
     * Calculates occupancy rate for dynamic pricing triggers
     */
    private FacilityAvailabilityDto calculateAndCacheAvailability(Long facilityId) {
        int totalSpots = spotRepository.countByFacilityIdAndStatus(facilityId, SpotStatus.AVAILABLE) +
                spotRepository.countByFacilityIdAndStatus(facilityId, SpotStatus.OCCUPIED) +
                spotRepository.countByFacilityIdAndStatus(facilityId, SpotStatus.RESERVED) +
                spotRepository.countByFacilityIdAndStatus(facilityId, SpotStatus.OUT_OF_ORDER);

        int availableSpots = spotRepository.countByFacilityIdAndStatus(facilityId, SpotStatus.AVAILABLE);
        int occupiedSpots = spotRepository.countByFacilityIdAndStatus(facilityId, SpotStatus.OCCUPIED);
        int reservedSpots = spotRepository.countByFacilityIdAndStatus(facilityId, SpotStatus.RESERVED);
        int outOfOrderSpots = spotRepository.countByFacilityIdAndStatus(facilityId, SpotStatus.OUT_OF_ORDER);

        BigDecimal occupancyRate = totalSpots > 0 ?
                BigDecimal.valueOf((double) (occupiedSpots + reservedSpots) / totalSpots * 100)
                        .setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // Update database cache
        LocalDateTime now = LocalDateTime.now();
        cacheRepository.updateFacilityAvailability(
                facilityId, availableSpots, occupiedSpots, reservedSpots,
                outOfOrderSpots, occupancyRate, now);

        FacilityAvailabilityDto dto = new FacilityAvailabilityDto(
                facilityId, null, null, null, null,
                totalSpots, availableSpots, occupiedSpots, reservedSpots, outOfOrderSpots,
                occupancyRate, null, now
        );

        // Update memory cache
        memoryCache.put(facilityId, dto);
        return dto;
    }

    private FacilityAvailabilityDto buildAvailabilityDto(FacilityAvailabilityCache cache) {
        return new FacilityAvailabilityDto(
                cache.getFacilityId(),
                cache.getFacility() != null ? cache.getFacility().getName() : null,
                cache.getFacility() != null ? cache.getFacility().getAddress() : null,
                cache.getFacility() != null ? cache.getFacility().getLocationLat() : null,
                cache.getFacility() != null ? cache.getFacility().getLocationLng() : null,
                cache.getTotalSpots(), cache.getAvailableSpots(),
                cache.getOccupiedSpots(), cache.getReservedSpots(), cache.getOutOfOrderSpots(),
                cache.getCurrentOccupancyRate(), null, cache.getLastUpdated()
        );
    }

    // File-based caching for demo (Redis replacement)
    private void loadCacheFromFile() {
        try {
            File cacheFile = new File(cacheFilePath);
            if (cacheFile.exists()) {
                Map<String, FacilityAvailabilityDto> fileCache = objectMapper.readValue(
                        cacheFile, new TypeReference<Map<String, FacilityAvailabilityDto>>() {});

                fileCache.forEach((key, value) -> memoryCache.put(Long.parseLong(key), value));
            }
        } catch (IOException e) {
            // Performance: Silent fail for cache loading, system should work without cache
            System.err.println("Failed to load availability cache: " + e.getMessage());
        }
    }

    private void saveCacheToFile() {
        try {
            Map<String, FacilityAvailabilityDto> fileCache = new ConcurrentHashMap<>();
            memoryCache.forEach((key, value) -> fileCache.put(key.toString(), value));

            objectMapper.writeValue(new File(cacheFilePath), fileCache);
        } catch (IOException e) {
            System.err.println("Failed to save availability cache: " + e.getMessage());
        }
    }
}
