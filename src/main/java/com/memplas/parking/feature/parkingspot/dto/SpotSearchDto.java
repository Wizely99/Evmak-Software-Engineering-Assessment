package com.memplas.parking.feature.parkingspot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memplas.parking.feature.parkingspot.model.SpotType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotSearchDto(
        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        BigDecimal latitude,
        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        BigDecimal longitude,
        @Min(value = 100, message = "Radius must be at least 100 meters")
        @Max(value = 50000, message = "Radius must not exceed 50km")
        Integer radiusMeters,
        SpotType preferredSpotType,
        LocalDateTime plannedStartTime,
        @Min(value = 15, message = "Duration must be at least 15 minutes")
        Integer plannedDurationMinutes,
        @Min(value = 100, message = "Vehicle height must be at least 100cm")
        Integer vehicleHeightCm
) {}
