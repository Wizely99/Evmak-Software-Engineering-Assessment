package com.memplas.parking.feature.paymentprocessing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.memplas.parking.feature.paymentprocessing.model.PaymentMethod;
import com.memplas.parking.feature.paymentprocessing.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentDto(
        @Schema(hidden = true)
        Long id,
        @NotNull(message = "Session ID is required")
        Long sessionId,
        @NotBlank(message = "Payment reference is required")
        @Size(max = 100, message = "Payment reference must not exceed 100 characters")
        String paymentReference,
        @Size(max = 255, message = "External payment ID must not exceed 255 characters")
        String externalPaymentId,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", message = "Amount must be non-negative")
        BigDecimal amount,
        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        String currency,
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,
        @Size(max = 50, message = "Payment provider must not exceed 50 characters")
        String paymentProvider,
        PaymentStatus status,
        @DecimalMin(value = "0.0", message = "Transaction fee must be non-negative")
        BigDecimal transactionFee,
        @Size(max = 255, message = "Failure reason must not exceed 255 characters")
        String failureReason,
        LocalDateTime processedAt,
        JsonNode callbackData,
        @Schema(hidden = true)
        Instant createdAt,
        @Schema(hidden = true)
        Instant updatedAt
) {}
