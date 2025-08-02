package com.memplas.parking.feature.facility.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DailyFacilityStatsDto(
        @Schema(hidden = true)
        Long id,
        @NotNull(message = "Facility ID is required")
        Long facilityId,
        String facilityName,
        @NotNull(message = "Date is required")
        LocalDate date,
        @Min(value = 0, message = "Total sessions must be non-negative")
        Integer totalSessions,
        @DecimalMin(value = "0.0", message = "Total revenue must be non-negative")
        BigDecimal totalRevenue,
        @Min(value = 0, message = "Average session duration must be non-negative")
        Integer avgSessionDurationMinutes,
        @DecimalMin(value = "0.0", message = "Peak occupancy rate must be non-negative")
        BigDecimal peakOccupancyRate,
        @Min(value = 0, message = "Violations count must be non-negative")
        Integer violationsCount,
        @Schema(hidden = true)
        Instant createdAt
) {}
