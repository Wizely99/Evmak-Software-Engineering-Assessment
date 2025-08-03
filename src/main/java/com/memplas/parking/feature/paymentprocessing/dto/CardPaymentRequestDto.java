package com.memplas.parking.feature.paymentprocessing.dto;
// Card Payment Request DTO


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CardPaymentRequestDto(
        @NotBlank(message = "Reference is required")
        String reference,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        @NotBlank(message = "Currency is required")
        String currency,
        @NotBlank(message = "Provider is required")
        String provider, // VISA, MASTERCARD

        @NotBlank(message = "Payment for is required")
        String paymentFor,
        @NotBlank(message = "Payer name is required")
        String payerName,
        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
        String cardNumber,
        @NotBlank(message = "Expiry month is required")
        @Pattern(regexp = "(0[1-9]|1[0-2])", message = "Expiry month must be 01-12")
        String expiryMonth,
        @NotBlank(message = "Expiry year is required")
        @Pattern(regexp = "\\d{4}", message = "Expiry year must be 4 digits")
        String expiryYear,
        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
        String cvv,
        @NotBlank(message = "Country code is required")
        String countryCode,
        String postalCode,
        String callbackUrl,
        String cancelUrl
) {}

