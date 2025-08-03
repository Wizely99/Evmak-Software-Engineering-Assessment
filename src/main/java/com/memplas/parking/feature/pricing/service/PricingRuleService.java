package com.memplas.parking.feature.pricing.service;

import com.memplas.parking.feature.parkingspot.model.SpotType;
import com.memplas.parking.feature.pricing.dto.PricingRuleDto;
import com.memplas.parking.feature.pricing.mapper.PricingRuleMapper;
import com.memplas.parking.feature.pricing.model.PricingRule;
import com.memplas.parking.feature.pricing.model.WeatherCondition;
import com.memplas.parking.feature.pricing.repository.PricingRuleRepository;
import com.memplas.parking.feature.vehicle.model.VehicleType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class PricingRuleService {
    private final PricingRuleRepository pricingRuleRepository;

    private final PricingRuleMapper pricingRuleMapper;

    private final WeatherService weatherService;

    private final EventService eventService;

    public PricingRuleService(PricingRuleRepository pricingRuleRepository,
                              PricingRuleMapper pricingRuleMapper,
                              WeatherService weatherService,
                              EventService eventService) {
        this.pricingRuleRepository = pricingRuleRepository;
        this.pricingRuleMapper = pricingRuleMapper;
        this.weatherService = weatherService;
        this.eventService = eventService;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculatePricing(Long facilityId, SpotType spotType,
                                       VehicleType vehicleType, int floorLevel,
                                       int hours, LocalDateTime startTime) {
        PricingRule rule = pricingRuleRepository.findByFacilityId(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("No pricing rule found for facility: " + facilityId));

        // Determine time-based conditions
        boolean isPeakHour = isPeakHour(startTime.toLocalTime());
        boolean isOffPeak = isOffPeakHour(startTime.toLocalTime());

        // Get external conditions
        WeatherCondition weather = weatherService.getCurrentWeather(facilityId);
        boolean isEvent = eventService.hasActiveEvent(facilityId, startTime);

        // Calculate pricing
        return rule.calculateRate(spotType, vehicleType, floorLevel,
                hours, weather, isEvent, isPeakHour, isOffPeak);
    }

    public PricingRuleDto createPricingRule(PricingRuleDto pricingRuleDto) {
        // Validate no existing rule for facility
        if (pricingRuleRepository.existsByFacilityId(pricingRuleDto.facilityId())) {
            throw new IllegalArgumentException("Pricing rule already exists for facility: " + pricingRuleDto.facilityId());
        }

        PricingRule rule = pricingRuleMapper.toEntity(pricingRuleDto);
        PricingRule savedRule = pricingRuleRepository.save(rule);
        evictPricingCache();

        return pricingRuleMapper.toDto(savedRule);
    }

    @Transactional(readOnly = true)
    public PricingRuleDto getPricingRule(Long id) {
        PricingRule rule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pricing rule not found: " + id));
        return pricingRuleMapper.toDto(rule);
    }

    @Transactional(readOnly = true)
    public PricingRuleDto getPricingRuleByFacility(Long facilityId) {
        PricingRule rule = pricingRuleRepository.findByFacilityId(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("No pricing rule for facility: " + facilityId));
        return pricingRuleMapper.toDto(rule);
    }

    @Transactional(readOnly = true)
    public List<PricingRule> getAllPricingRules() {
        return pricingRuleRepository.findAll();
    }

    public PricingRuleDto updatePricingRule(Long id, PricingRuleDto pricingRuleDto) {
        PricingRule updatedRule = pricingRuleMapper.toEntity(pricingRuleDto);
        updatedRule.setId(id);

        PricingRule savedRule = pricingRuleRepository.save(updatedRule);
        evictPricingCache();

        return pricingRuleMapper.toDto(savedRule);
    }

    public void deletePricingRule(Long id) {
        if (!pricingRuleRepository.existsById(id)) {
            throw new EntityNotFoundException("Pricing rule not found: " + id);
        }
        pricingRuleRepository.deleteById(id);
        evictPricingCache();
    }

    // Quick rate lookup for mobile apps
    @Transactional(readOnly = true)
    public BigDecimal getQuickRate(Long facilityId, SpotType spotType, VehicleType vehicleType) {
        PricingRule rule = pricingRuleRepository.findByFacilityId(facilityId)
                .orElseThrow(() -> new EntityNotFoundException("No pricing rule found for facility: " + facilityId));

        BigDecimal baseRate = switch (spotType) {
            case VIP -> rule.getVipRate();
            case EV_CHARGING -> rule.getEvChargingRate();
            default -> rule.getBaseRate();
        };

        // Apply vehicle type multiplier
        return baseRate.multiply(getVehicleMultiplier(rule, vehicleType));
    }

    private boolean isPeakHour(LocalTime time) {
        return (time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(9, 0))) ||
                (time.isAfter(LocalTime.of(17, 0)) && time.isBefore(LocalTime.of(20, 0)));
    }

    private boolean isOffPeakHour(LocalTime time) {
        return time.isAfter(LocalTime.of(22, 0)) || time.isBefore(LocalTime.of(6, 0));
    }

    private BigDecimal getVehicleMultiplier(PricingRule rule, VehicleType vehicleType) {
        return switch (vehicleType) {
            case MOTORCYCLE -> rule.getMotorcycleDiscount();
            case TRUCK -> rule.getTruckSurcharge();
            default -> BigDecimal.ONE;
        };
    }

    @CacheEvict(value = {"pricingEstimate", "quickRate"}, allEntries = true)
    private void evictPricingCache() {
        // Cache eviction handled by annotation
    }
}

