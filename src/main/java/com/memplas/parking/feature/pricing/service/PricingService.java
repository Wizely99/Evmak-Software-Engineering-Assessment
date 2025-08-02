package com.memplas.parking.feature.pricing.service;

import com.memplas.parking.feature.parkingspot.service.AvailabilityCacheService;
import com.memplas.parking.feature.pricing.model.PricingRule;
import com.memplas.parking.feature.pricing.repository.PricingRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PricingService {
    private final PricingRuleRepository pricingRuleRepository;

    private final AvailabilityCacheService cacheService;

    public PricingService(PricingRuleRepository pricingRuleRepository, AvailabilityCacheService cacheService) {
        this.pricingRuleRepository = pricingRuleRepository;
        this.cacheService = cacheService;
    }

    /**
     * Performance: Single query to get applicable pricing rules
     * Implements dynamic pricing based on occupancy rates
     */
    public BigDecimal calculateEstimatedAmount(Long facilityId, Integer durationMinutes) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Performance: Get applicable rules in single query with proper ordering
        List<PricingRule> applicableRules = pricingRuleRepository.findApplicableRules(
                facilityId, currentDate, currentTime);

        if (applicableRules.isEmpty()) {
            // Fallback to default rate if no rules found
            return BigDecimal.valueOf(1000.00); // 1000 TZS default
        }

        PricingRule rule = applicableRules.getFirst(); // Most specific rule first
        BigDecimal baseRate = rule.getBaseRate();

        // Apply demand-based pricing multiplier
        var availability = cacheService.getFacilityAvailability(List.of(facilityId)).getFirst();
        if (availability.currentOccupancyRate() >= rule.getOccupancyThreshold()) {
            baseRate = baseRate.multiply(rule.getDemandMultiplier());
        }


        // Calculate total amount (hourly rate * duration in hours)
        BigDecimal hours = BigDecimal.valueOf(durationMinutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = baseRate.multiply(hours);

        // Apply daily rate cap if configured
        if (rule.getMaxDailyRate() != null && totalAmount.compareTo(rule.getMaxDailyRate()) > 0) {
            totalAmount = rule.getMaxDailyRate();
        }

        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
