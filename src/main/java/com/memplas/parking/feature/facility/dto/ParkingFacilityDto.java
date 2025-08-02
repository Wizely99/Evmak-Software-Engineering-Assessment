package com.memplas.parking.feature.facility.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.memplas.parking.feature.facility.model.FacilityStatus;
import com.memplas.parking.feature.facility.model.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParkingFacilityDto(
        @Schema(hidden = true)
        Long id,
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,
        @NotNull(message = "Facility type is required")
        FacilityType facilityType,
        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address,
        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        Double locationLat,
        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        Double locationLng,
        @NotNull(message = "Total spots is required")
        @Min(value = 1, message = "Total spots must be at least 1")
        Integer totalSpots,
        LocalTime operatingHoursStart,
        LocalTime operatingHoursEnd,
        FacilityStatus status,
        JsonNode features,
        @Min(value = 100, message = "Max height must be at least 100cm")
        Integer maxHeightCm,
        @Schema(hidden = true)
        Instant createdAt,
        @Schema(hidden = true)
        Instant updatedAt
) {}
