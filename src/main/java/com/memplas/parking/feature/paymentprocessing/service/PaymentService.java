package com.memplas.parking.feature.paymentprocessing.service;

import com.memplas.parking.core.exception.PaymentProcessingException;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import com.memplas.parking.feature.parkingsession.repository.ParkingSessionRepository;
import com.memplas.parking.feature.paymentprocessing.dto.CardPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.MobileMoneyPaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentResponseDto;
import com.memplas.parking.feature.paymentprocessing.mapper.PaymentMapper;
import com.memplas.parking.feature.paymentprocessing.model.Payment;
import com.memplas.parking.feature.paymentprocessing.model.PaymentProvider;
import com.memplas.parking.feature.paymentprocessing.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final ParkingSessionRepository sessionRepository;

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final CardPaymentService cardPaymentService;

    private final MobileMoneyPaymentService mobileMoneyPaymentService;

    public PaymentService(ParkingSessionRepository sessionRepository,
                          PaymentRepository paymentRepository,
                          PaymentMapper paymentMapper,
                          CardPaymentService cardPaymentService,
                          MobileMoneyPaymentService mobileMoneyPaymentService) {
        this.sessionRepository = sessionRepository;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.cardPaymentService = cardPaymentService;
        this.mobileMoneyPaymentService = mobileMoneyPaymentService;
    }

    public PaymentDto payForSession(Long sessionId, PaymentProvider provider,
                                    CardPaymentRequestDto cardRequest,
                                    MobileMoneyPaymentRequestDto mobileRequest) {

        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionId));

        if (paymentRepository.existsBySessionId(sessionId)) {
            throw new IllegalStateException("Session already paid");
        }

        String reference = generatePaymentReference(sessionId);
        PaymentResponseDto response = sendPaymentRequest(provider, cardRequest, mobileRequest);

        if (!Boolean.TRUE.equals(response.status())) {
            throw new PaymentProcessingException("Payment failed: " + response.message());
        }

        Payment payment = createPaymentEntity(session, provider, reference, response, cardRequest, mobileRequest);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    private String generatePaymentReference(Long sessionId) {
        return "PARK-" + sessionId + "-" + System.currentTimeMillis();
    }

    private PaymentResponseDto sendPaymentRequest(PaymentProvider provider,
                                                  CardPaymentRequestDto cardRequest,
                                                  MobileMoneyPaymentRequestDto mobileRequest) {
        if (provider.isCardPayment()) {
            System.out.println("payment for " + cardRequest);
            return cardPaymentService.pay(cardRequest);
        } else {
            return mobileMoneyPaymentService.pay(mobileRequest);
        }
    }

    private Payment createPaymentEntity(ParkingSession session, PaymentProvider provider,
                                        String reference, PaymentResponseDto response,
                                        CardPaymentRequestDto cardRequest,
                                        MobileMoneyPaymentRequestDto mobileRequest) {
        Payment payment = new Payment();
        payment.setSession(session);
        payment.setPaymentReference(reference);
        payment.setExternalPaymentId(response.transactionId());
        payment.setAmount(cardRequest != null ? cardRequest.amount() : mobileRequest.amount());
        payment.setCurrency("TZS");
        payment.setPaymentProvider(provider);
        payment.setPaymentMethod(response.method());
        return payment;
    }

    @Transactional(readOnly = true)
    public boolean isSessionPaid(Long sessionId) {
        return paymentRepository.existsBySessionId(sessionId);
    }
}
