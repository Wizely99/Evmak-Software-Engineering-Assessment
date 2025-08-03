package com.memplas.parking.feature.paymentprocessing.dto;

import com.memplas.parking.feature.paymentprocessing.model.PaymentMethod;

public record PaymentResponseDto(
        Boolean status,
        String message,
        String reference,
        String transactionId,
        String ussdCode,
        String instructions,
        PaymentMethod method
) {
    public static PaymentResponseDto success(String reference, String transactionId, String message, PaymentMethod method) {
        return new PaymentResponseDto(true, message, reference, transactionId, null, null, method);
    }

    // Success response for mobile money
    public static PaymentResponseDto successMobileMoney(String reference, String transactionId,
                                                        String message, String ussdCode, String instructions) {
        return new PaymentResponseDto(true, message, reference, transactionId, ussdCode, instructions, PaymentMethod.MOBILE_MONEY);
    }

    // Failure response factory
    public static PaymentResponseDto failure(String reference, String transactionId, String message) {
        return new PaymentResponseDto(false, message, reference, transactionId, null, null, null);
    }
}
