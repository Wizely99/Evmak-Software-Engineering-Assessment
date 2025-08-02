package com.memplas.parking.feature.parkingspot.service;//package com.memplas.parking.feature.parkingspot.service;

import com.memplas.parking.core.config.cache.CacheKeys;
import com.memplas.parking.feature.parkingspot.dto.FacilityAvailabilityDto;
import com.memplas.parking.feature.parkingspot.dto.FacilitySpotCounts;
import com.memplas.parking.feature.parkingspot.repository.ParkingSpotRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Performance-Critical Service: Manages availability caching for sub-50ms responses
 * Uses lazy caching with 1-minute TTL - no aggressive cache updates
 * Cache is calculated only when needed and expires naturally
 */
@Service
@Transactional
public class AvailabilityCacheService {
    private final ParkingSpotRepository spotRepository;

    private final CacheManager cacheManager;

    public AvailabilityCacheService(
            ParkingSpotRepository spotRepository, CacheManager cacheManager) {
        this.spotRepository = spotRepository;
        this.cacheManager = cacheManager;
    }

    public void recalculateCache(Cache cache) {
        List<FacilityAvailabilityDto> results = calculateAndCacheAvailability();


        results.forEach(availabilityDto ->
                cache.put(availabilityDto.facilityId(), availabilityDto)
        );

    }

    public List<FacilityAvailabilityDto> getFacilityAvailability(List<Long> ids) {
        Cache cache = cacheManager.getCache(CacheKeys.FACILITY_AVAILABILITY_CACHE);
        if (cache == null) throw new IllegalStateException("Facility availability cache not configured");

        try {
            return getList(ids, cache);
        } catch (Exception e) {
            recalculateCache(cache);
            return getList(ids, cache);
        }
    }

    //todo handle recalculating ache without throwing an error
    private static List<FacilityAvailabilityDto> getList(List<Long> ids, Cache cache) {
        return ids.stream()
                .map(id -> Objects.requireNonNull(
                        cache.get(id, FacilityAvailabilityDto.class),
                        "No data found for id: " + id))
                .toList();
    }

    /**
     * Evict all cache - use only for system-wide updates
     */
    @CacheEvict(value = CacheKeys.FACILITY_AVAILABILITY_CACHE, allEntries = true)
    public void evictAllCache() {
    }

    /**
     * Performance: Optimized real-time calculation
     * Uses efficient queries and updates database cache for persistence
     */
    private List<FacilityAvailabilityDto> calculateAndCacheAvailability() {
        List<FacilitySpotCounts> availableSpots = spotRepository.getAllFacilitiesSpotCounts();
        var now = Instant.now();
        //TODO CALCULATE THE RATE
        //IMPLEMENTATION STEPS
//        PASS ALL CONDITIONS TO PRICING RULE AND RETURN ALL AT ONCE FROM DB

        var facilities = availableSpots.stream().map(spot -> {
            var totalSpots = spot.getTotalSpots();
            Integer occupancyRate = totalSpots > 0 ? (int) ((spot.occupiedSpots() + spot.reservedSpots()) * 100.0 / totalSpots) : 0;
            return new FacilityAvailabilityDto(spot.facilityId(), totalSpots, spot.availableSpots(), spot.occupiedSpots(), spot.reservedSpots(), spot.outOfOrderSpots(), occupancyRate, null, now);
        });


        return facilities.collect(Collectors.toList());
    }


}
