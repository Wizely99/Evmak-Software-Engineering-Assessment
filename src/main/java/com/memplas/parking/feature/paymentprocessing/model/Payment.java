package com.memplas.parking.feature.paymentprocessing.model;

import com.memplas.parking.core.model.BaseEntity;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @NotNull(message = "Parking session is required")
    private ParkingSession session;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Payment reference is required")
    @Size(max = 100, message = "Payment reference must not exceed 100 characters")
    private String paymentReference;

    @Column(length = 255)
    @Size(max = 255, message = "External payment ID must not exceed 255 characters")
    private String externalPaymentId;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "TZS";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Payment provider is required")
    private PaymentProvider paymentProvider;

    // Constructors
    public Payment() {}

    public Payment(Long id) {
        super(id);
    }

    // Getters and Setters
    public ParkingSession getSession() {return session;}

    public void setSession(ParkingSession session) {this.session = session;}

    public String getPaymentReference() {return paymentReference;}

    public void setPaymentReference(String paymentReference) {this.paymentReference = paymentReference;}

    public String getExternalPaymentId() {return externalPaymentId;}

    public void setExternalPaymentId(String externalPaymentId) {this.externalPaymentId = externalPaymentId;}

    public BigDecimal getAmount() {return amount;}

    public void setAmount(BigDecimal amount) {this.amount = amount;}

    public String getCurrency() {return currency;}

    public void setCurrency(String currency) {this.currency = currency;}

    public PaymentMethod getPaymentMethod() {return paymentMethod;}

    public void setPaymentMethod(PaymentMethod paymentMethod) {this.paymentMethod = paymentMethod;}

    public PaymentProvider getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(PaymentProvider paymentProvider) {
        this.paymentProvider = paymentProvider;
    }
}

