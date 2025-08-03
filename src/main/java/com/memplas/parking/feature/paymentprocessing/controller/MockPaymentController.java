package com.memplas.parking.feature.paymentprocessing.controller;

import com.memplas.parking.feature.paymentprocessing.dto.CardPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.MobileMoneyPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentResponseDto;
import com.memplas.parking.feature.paymentprocessing.service.MockPaymentService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/mock-payment/v1")
public class MockPaymentController {
    private final MockPaymentService mockPaymentService;

    public MockPaymentController(MockPaymentService mockPaymentService) {
        this.mockPaymentService = mockPaymentService;
    }

    @PostMapping("/card-payment")
    public ResponseEntity<PaymentResponseDto> processCardPayment(@Valid @RequestBody CardPaymentRequestDto request) {
        PaymentResponseDto response = mockPaymentService.processCardPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mobile-money-payment")
    public ResponseEntity<PaymentResponseDto> processMobileMoneyPayment(@Valid @RequestBody MobileMoneyPaymentRequestDto request) {
        PaymentResponseDto response = mockPaymentService.processMobileMoneyPayment(request);
        return ResponseEntity.ok(response);
    }
}
