package com.memplas.parking.feature.paymentprocessing.controller;

import com.memplas.parking.feature.paymentprocessing.dto.PaymentCallbackDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Validated
@Tag(name = "Payments", description = "Payment processing with X-PAYMENT-PROVIDER integration")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Process payment", description = "Processes payment for parking session using card or mobile money. Supports VISA, MASTERCARD, VodaCom, AirtelMoney, MixByYass, Halotel.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment initiated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment data or unsupported provider"),
            @ApiResponse(responseCode = "403", description = "Access denied - not session owner"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    public PaymentDto processPayment(@Valid @RequestBody PaymentRequestDto request) {
        return paymentService.processPayment(request);
    }

    @PostMapping("/callback")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Payment callback", description = "Webhook endpoint for X-PAYMENT-PROVIDER callbacks. Public endpoint for payment status updates.")
    @ApiResponse(responseCode = "200", description = "Callback processed successfully")
    public void handlePaymentCallback(@RequestBody PaymentCallbackDto callback) {
        paymentService.handlePaymentCallback(callback);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user payments", description = "Retrieves payment history for current user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Payment history retrieved successfully")
    public List<PaymentDto> getCurrentUserPayments() {
        return paymentService.getCurrentUserPayments();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment details by ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public PaymentDto getPaymentById(@Parameter(description = "Payment ID") @PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves all payments. Requires ADMIN role.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All payments retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    public List<PaymentDto> getAllPayments() {
        return paymentService.getAllPayments();
    }
}
