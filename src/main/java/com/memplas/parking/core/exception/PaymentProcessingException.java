package com.memplas.parking.core.exception;

// Custom Exception
public class PaymentProcessingException extends RuntimeException {
    public PaymentProcessingException(String message) {
        super(message);
    }
}
