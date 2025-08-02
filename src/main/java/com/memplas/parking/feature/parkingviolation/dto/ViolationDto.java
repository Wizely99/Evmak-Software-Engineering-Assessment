package com.memplas.parking.feature.parkingviolation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.memplas.parking.feature.parkingviolation.model.ViolationStatus;
import com.memplas.parking.feature.parkingviolation.model.ViolationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViolationDto(
        @Schema(hidden = true)
        Long id,
        Long sessionId,
        @NotNull(message = "Vehicle ID is required")
        Long vehicleId,
        @NotNull(message = "Spot ID is required")
        Long spotId,
        @NotNull(message = "Violation type is required")
        ViolationType violationType,
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,
        @NotNull(message = "Fine amount is required")
        @DecimalMin(value = "0.0", message = "Fine amount must be non-negative")
        BigDecimal fineAmount,
        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        String currency,
        ViolationStatus status,
        @NotNull(message = "Detection time is required")
        LocalDateTime detectedAt,
        @Size(max = 100, message = "Reported by must not exceed 100 characters")
        String reportedBy,
        JsonNode evidenceUrls,
        LocalDateTime resolvedAt,
        @Schema(hidden = true)
        Instant createdAt,
        @Schema(hidden = true)
        Instant updatedAt
) {}
