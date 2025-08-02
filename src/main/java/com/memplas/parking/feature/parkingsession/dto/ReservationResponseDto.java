package com.memplas.parking.feature.parkingsession.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationResponseDto(
        String sessionReference,
        Long spotId,
        String spotNumber,
        String facilityName,
        LocalDateTime reservationExpiry,
        BigDecimal estimatedAmount,
        String currency,
        String status,
        String message
) {}
