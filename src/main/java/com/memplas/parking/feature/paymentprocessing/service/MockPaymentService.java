package com.memplas.parking.feature.paymentprocessing.service;

import com.memplas.parking.feature.paymentprocessing.dto.CardPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.MobileMoneyPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentResponseDto;
import com.memplas.parking.feature.paymentprocessing.model.PaymentMethod;
import org.springframework.stereotype.Service;

@Service
public class MockPaymentService {
    public PaymentResponseDto processCardPayment(CardPaymentRequestDto request) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String transactionId = "TXN-" + System.currentTimeMillis();

        // 90% success rate for testing
        boolean isSuccess = Math.random() < 0.9;

        if (isSuccess) {
            return PaymentResponseDto.success(
                    request.reference(),
                    transactionId,
                    "Payment processed successfully",
                    PaymentMethod.CARD
            );
        } else {
            return PaymentResponseDto.failure(
                    request.reference(),
                    transactionId,
                    "Insufficient funds"
            );
        }
    }

    public PaymentResponseDto processMobileMoneyPayment(MobileMoneyPaymentRequestDto request) {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String transactionId = "TXN-MM-" + System.currentTimeMillis();

        // 85% success rate for testing
        boolean isSuccess = Math.random() < 0.85;

        if (isSuccess) {
            String ussdCode = generateUssdCode(request.mno());
            String instructions = "Dial " + ussdCode + " on your " + request.mno() + " registered phone";

            return PaymentResponseDto.successMobileMoney(
                    request.reference(),
                    transactionId,
                    "Mobile money payment initiated successfully",
                    ussdCode,
                    instructions
            );
        } else {
            return PaymentResponseDto.failure(
                    request.reference(),
                    transactionId,
                    "Mobile wallet not found"
            );
        }
    }

    private String generateUssdCode(String mno) {
        return switch (mno.toUpperCase()) {
            case "VODACOM" -> "*150*01*" + System.currentTimeMillis() % 100000 + "#";
            case "AIRTELMONEY" -> "*150*60*" + System.currentTimeMillis() % 100000 + "#";
            case "MIXBYYASS" -> "*150*71*" + System.currentTimeMillis() % 100000 + "#";
            case "HALOTEL" -> "*150*88*" + System.currentTimeMillis() % 100000 + "#";
            default -> "*150*01*12345#";
        };
    }
}

