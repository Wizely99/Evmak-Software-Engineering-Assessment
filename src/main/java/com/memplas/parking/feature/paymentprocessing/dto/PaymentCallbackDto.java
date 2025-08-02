package com.memplas.parking.feature.paymentprocessing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentCallbackDto(
        String status,
        String message,
        String paymentId,
        String reference,
        String externalPaymentId,
        String errorCode,
        JsonNode data
) {}
