package com.memplas.parking.feature.parkingspot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FacilityAvailabilityDto(
        @NotNull(message = "Facility ID is required")
        Long facilityId,
        @NotNull(message = "Total spots is required")
        @Min(value = 0, message = "Total spots must be non-negative")
        Long totalSpots,
        @NotNull(message = "Available spots is required")
        @Min(value = 0, message = "Available spots must be non-negative")
        Long availableSpots,
        @Min(value = 0, message = "Occupied spots must be non-negative")
        Long occupiedSpots,
        @Min(value = 0, message = "Reserved spots must be non-negative")
        Long reservedSpots,
        @Min(value = 0, message = "Out of order spots must be non-negative")
        Long outOfOrderSpots,
        @NotNull(message = "Current occupancy rate is required")
        @Min(value = 0, message = "Occupancy rate must be between 0 and 100 inclusive")
        @Max(value = 100, message = "Occupancy rate must be between 0 and 100 inclusive")
        Integer currentOccupancyRate,
        BigDecimal currentHourlyRate,
        Instant lastUpdated
) {}
