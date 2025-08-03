package com.memplas.parking.feature.paymentprocessing.controller;

import com.memplas.parking.feature.paymentprocessing.dto.CardPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.MobileMoneyPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentDto;
import com.memplas.parking.feature.paymentprocessing.model.PaymentProvider;
import com.memplas.parking.feature.paymentprocessing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Payment processing operations")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/sessions/{sessionId}/card-payment")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Pay for parking session with card")
    public PaymentDto payWithCard(@PathVariable Long sessionId,
                                  @RequestParam PaymentProvider provider,
                                  @Valid @RequestBody CardPaymentRequestDto request) {
        return paymentService.payForSession(sessionId, provider, request, null);
    }

    @PostMapping("/sessions/{sessionId}/mobile-money")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Pay for parking session with mobile money")
    public PaymentDto payWithMobileMoney(@PathVariable Long sessionId,
                                         @RequestParam PaymentProvider provider,
                                         @Valid @RequestBody MobileMoneyPaymentRequestDto request) {
        return paymentService.payForSession(sessionId, provider, null, request);
    }

    @GetMapping("/sessions/{sessionId}/status")
    @Operation(summary = "Check if session is paid")
    public ResponseEntity<PaymentStatusDto> getPaymentStatus(@PathVariable Long sessionId) {
        boolean isPaid = paymentService.isSessionPaid(sessionId);
        return ResponseEntity.ok(new PaymentStatusDto(sessionId, isPaid));
    }
}

