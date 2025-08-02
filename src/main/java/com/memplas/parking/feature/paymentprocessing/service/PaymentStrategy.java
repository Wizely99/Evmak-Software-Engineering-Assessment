package com.memplas.parking.feature.paymentprocessing.service;

import com.memplas.parking.feature.paymentprocessing.dto.PaymentCallbackDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.model.Payment;

public interface PaymentStrategy {
    Payment processPayment(PaymentRequestDto request, String sessionReference);

    void handleCallback(PaymentCallbackDto callback);

    boolean supports(String paymentProvider);
}
