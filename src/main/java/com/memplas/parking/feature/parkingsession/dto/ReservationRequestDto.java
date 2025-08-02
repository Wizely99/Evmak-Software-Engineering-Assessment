package com.memplas.parking.feature.parkingsession.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationRequestDto(
        @NotNull(message = "Spot ID is required")
        Long spotId,
        @NotNull(message = "Vehicle ID is required")
        Long vehicleId,
        @NotNull(message = "Start time is required")
        LocalDateTime startTime,
        @Min(value = 15, message = "Duration must be at least 15 minutes")
        Integer plannedDurationMinutes
) {}
