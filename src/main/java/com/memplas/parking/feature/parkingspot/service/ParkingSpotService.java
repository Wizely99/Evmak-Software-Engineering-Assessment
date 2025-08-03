package com.memplas.parking.feature.parkingspot.service;

import com.memplas.parking.feature.parkingspot.dto.ParkingSpotDto;
import com.memplas.parking.feature.parkingspot.mapper.ParkingSpotMapper;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
import com.memplas.parking.feature.parkingspot.model.SpotStatus;
import com.memplas.parking.feature.parkingspot.model.SpotType;
import com.memplas.parking.feature.parkingspot.repository.ParkingSpotRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public ParkingSpotDto createSpot(ParkingSpotDto spotDto) {
        ParkingSpot spot = spotMapper.toEntity(spotDto);
        ParkingSpot savedSpot = spotRepository.save(spot);
        cacheService.evictAllCache();
        return spotMapper.toDto(savedSpot);
    }

    @Transactional(readOnly = true)
    public ParkingSpotDto getSpotById(Long id) {
        ParkingSpot spot = spotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parking spot not found with id: " + id));
        return spotMapper.toDto(spot);
    }

    public ParkingSpotDto updateSpot(Long id, ParkingSpotDto spotDto) {
        ParkingSpot updatedSpot = spotMapper.toEntity(spotDto);
        updatedSpot.setId(id);
        ParkingSpot savedSpot = spotRepository.save(updatedSpot);

        return spotMapper.toDto(savedSpot);
    }

    public void deleteSpot(Long id) {
        if (!spotRepository.existsById(id)) {
            throw new EntityNotFoundException("Parking spot not found with id: " + id);
        }
        spotRepository.deleteById(id);
    }

    @Scheduled(fixedRate = 120000) // 2 minutes
    public void cleanupExpiredReservations() {
        LocalDateTime expiredTime = LocalDateTime.now();

        List<ParkingSpot> expiredSpots = spotRepository.findExpiredReservations(expiredTime);
        if (!expiredSpots.isEmpty()) {
            int releasedCount = spotRepository.releaseExpiredReservations(expiredTime);
            cacheService.evictAllCache();
            System.out.printf("Released %d expired reservations%n", releasedCount);
        }
    }

    @Transactional(readOnly = true)
    public Page<ParkingSpotDto> findByFilters(Long facilityId, Long floorId, SpotType spotType, SpotStatus spotStatus, Pageable pageable) {
        Specification<ParkingSpot> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (facilityId != null) {
                predicates.add(cb.equal(root.get("facility").get("id"), facilityId));
            }

            if (floorId != null) {
                predicates.add(cb.equal(root.get("floor").get("id"), floorId));
            }

            if (spotType != null) {
                predicates.add(cb.equal(root.get("type"), spotType));
            }

            if (spotStatus != null) {
                predicates.add(cb.equal(root.get("status"), spotStatus));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return spotRepository.findAll(spec, pageable)
                .map(spotMapper::toDto);
    }


}