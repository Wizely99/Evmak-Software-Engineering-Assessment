package com.memplas.parking.feature.pricing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PricingRuleDto(
        @Schema(hidden = true)
        Long id,
        @NotNull(message = "Facility ID is required")
        Long facilityId,
        @NotBlank(message = "Rule name is required")
        @Size(max = 100, message = "Rule name must not exceed 100 characters")
        String ruleName,
        @NotNull(message = "Base rate is required")
        @DecimalMin(value = "0.0", message = "Base rate must be non-negative")
        BigDecimal baseRate,
        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        String currency,
        LocalTime timeOfDayStart,
        LocalTime timeOfDayEnd,
        @Pattern(regexp = "^[01]{7}$", message = "Day of week mask must be 7 binary digits (MTWTFSS)")
        String dayOfWeekMask,
        @DecimalMin(value = "0.1", message = "Demand multiplier must be at least 0.1")
        @DecimalMax(value = "10.0", message = "Demand multiplier must not exceed 10.0")
        BigDecimal demandMultiplier,
        @Min(value = 1, message = "Occupancy threshold must be at least 1%")
        @Max(value = 100, message = "Occupancy threshold must not exceed 100%")
        Integer occupancyThreshold,
        @DecimalMin(value = "0.0", message = "Max daily rate must be non-negative")
        BigDecimal maxDailyRate,
        @Min(value = 0, message = "Grace period must be non-negative")
        Integer gracePeriodMinutes,
        Boolean isActive,
        @NotNull(message = "Effective from date is required")
        LocalDate effectiveFrom,
        LocalDate effectiveUntil,
        @Schema(hidden = true)
        Instant createdAt,
        @Schema(hidden = true)
        Instant updatedAt
) {}
