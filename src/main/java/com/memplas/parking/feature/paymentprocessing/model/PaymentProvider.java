package com.memplas.parking.feature.paymentprocessing.model;

public enum PaymentProvider {
    VISA("Card Payment"),
    MASTERCARD("Card Payment"),
    // Mobile money providers
    VODACOM("Mobile Money"),
    AIRTEL_MONEY("Mobile Money"),
    MIX_BY_YASS("Mobile Money"), // Tigo Pesa
    HALOTEL("Mobile Money");

    private final String paymentType;

    PaymentProvider(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public boolean isCardPayment() {
        return "Card Payment".equals(paymentType);
    }

    public boolean isMobileMoneyPayment() {
        return "Mobile Money".equals(paymentType);
    }
}
