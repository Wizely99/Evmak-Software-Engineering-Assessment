package com.memplas.parking.feature.paymentprocessing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MobileMoneyPaymentRequestDto(
        @NotBlank(message = "Reference is required")
        String reference,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        @NotBlank(message = "Currency is required")
        String currency,
        @NotBlank(message = "MNO is required")
        String mno, // VodaCom, AirtelMoney, MixByYass, Halotel

        @NotBlank(message = "Payment for is required")
        String paymentFor,
        @NotBlank(message = "Payer name is required")
        String payerName,
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Invalid phone number format")
        String phoneNumber,
        String callbackUrl,
        String cancelUrl
) {}
