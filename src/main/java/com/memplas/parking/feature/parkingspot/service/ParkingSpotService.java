package com.memplas.parking.feature.parkingspot.service;

import com.memplas.parking.feature.parkingspot.dto.ParkingSpotDto;
import com.memplas.parking.feature.parkingspot.mapper.ParkingSpotMapper;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
import com.memplas.parking.feature.parkingspot.model.SpotType;
import com.memplas.parking.feature.parkingspot.repository.ParkingSpotRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ParkingSpotService {
    private final ParkingSpotRepository spotRepository;

    private final ParkingSpotMapper spotMapper;

    private final AvailabilityCacheService cacheService;

    public ParkingSpotService(
            ParkingSpotRepository spotRepository,
            ParkingSpotMapper spotMapper,
            AvailabilityCacheService cacheService) {
        this.spotRepository = spotRepository;
        this.spotMapper = spotMapper;
        this.cacheService = cacheService;
    }

    @Transactional(readOnly = true)
    public List<ParkingSpotDto> findAvailableSpots(Long facilityId, SpotType spotType) {
        List<ParkingSpot> spots = spotRepository.findAvailableSpots(facilityId, spotType);
        return spots.stream().map(spotMapper::toDto).toList();
    }

    // Performance: Scheduled cleanup of expired reservations to maintain data consistency
    // Runs every 2 minutes to ensure 15-minute reservation window is respected
    @Scheduled(fixedRate = 120000) // 2 minutes
    public void cleanupExpiredReservations() {
        LocalDateTime expiredTime = LocalDateTime.now();

        // Performance: Batch update in single query instead of individual updates
        List<ParkingSpot> expiredSpots = spotRepository.findExpiredReservations(expiredTime);
        if (!expiredSpots.isEmpty()) {
            int releasedCount = spotRepository.releaseExpiredReservations(expiredTime);

            cacheService.evictAllCache();

            System.out.printf("Released %d expired reservations%n", releasedCount);
        }
    }
}
