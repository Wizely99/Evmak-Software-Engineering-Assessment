package com.memplas.parking.feature.paymentprocessing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memplas.parking.feature.paymentprocessing.model.PaymentMethod;
import com.memplas.parking.feature.paymentprocessing.model.PaymentProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentDto(
        @Schema(hidden = true)
        Long id,
        @NotNull(message = "Session ID is required")
        @Positive(message = "Session ID must be positive")
        @Schema(description = "Parking session ID", example = "123")
        Long sessionId,
        @NotBlank(message = "Payment reference is required")
        @Size(max = 100, message = "Payment reference must not exceed 100 characters")
        @Schema(description = "Unique payment reference", example = "PARK-001-20250802")
        String paymentReference,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        @Schema(description = "Payment amount", example = "5000.00")
        BigDecimal amount,
        @NotNull(message = "Payment method is required")
        @Schema(description = "Payment method")
        PaymentMethod paymentMethod,
        @NotNull(message = "Payment provider is required")
        @Schema(description = "Payment provider")
        PaymentProvider paymentProvider,
        @Schema(description = "Currency code", example = "TZS")
        String currency,
        @Schema(hidden = true)
        String externalPaymentId,
        @Schema(hidden = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        Instant createdAt,
        @Schema(hidden = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        Instant updatedAt
) {}