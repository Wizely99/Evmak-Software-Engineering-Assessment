package com.memplas.parking.feature.paymentprocessing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memplas.parking.feature.paymentprocessing.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentRequestDto(
        @NotBlank(message = "Session reference is required")
        String sessionReference,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,
        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        String currency,
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,
        @Size(max = 50, message = "Payment provider must not exceed 50 characters")
        String paymentProvider,
        @NotBlank(message = "Payer name is required")
        @Size(max = 100, message = "Payer name must not exceed 100 characters")
        String payerName,
        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String phoneNumber,
        @NotBlank(message = "Callback URL is required")
        String callbackUrl,
        @NotBlank(message = "Cancel URL is required")
        String cancelUrl,
        @NotBlank(message = "payment reference is required")
        String paymentReference
) {}
