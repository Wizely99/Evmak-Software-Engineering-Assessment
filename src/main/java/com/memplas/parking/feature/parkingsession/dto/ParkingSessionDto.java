package com.memplas.parking.feature.parkingsession.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memplas.parking.feature.parkingsession.model.SessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParkingSessionDto(
        @Schema(hidden = true)
        Long id,
        @NotBlank(message = "Session reference is required")
        @Size(max = 50, message = "Session reference must not exceed 50 characters")
        @Pattern(regexp = "^PACK-\\d{6}$", message = "Session reference must follow PACK-XXXXXX format")
        String sessionReference,
        @NotNull(message = "User ID is required")
        Long userId,
        @NotNull(message = "Vehicle ID is required")
        Long vehicleId,
        @NotNull(message = "Spot ID is required")
        Long spotId,
        @NotNull(message = "Start time is required")
        LocalDateTime startTime,
        LocalDateTime endTime,
        @Min(value = 1, message = "Planned duration must be at least 1 minute")
        Integer plannedDurationMinutes,
        Integer actualDurationMinutes,
        SessionStatus status,
        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.0", message = "Total amount must be non-negative")
        BigDecimal totalAmount,
        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        String currency,
        @Schema(hidden = true)
        Instant createdAt,
        @Schema(hidden = true)
        Instant updatedAt
) {}
