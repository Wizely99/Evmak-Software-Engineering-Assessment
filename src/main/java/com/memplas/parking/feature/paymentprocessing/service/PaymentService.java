package com.memplas.parking.feature.paymentprocessing.service;

import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import com.memplas.parking.feature.parkingsession.repository.ParkingSessionRepository;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentCallbackDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentDto;
import com.memplas.parking.feature.paymentprocessing.dto.PaymentRequestDto;
import com.memplas.parking.feature.paymentprocessing.mapper.PaymentMapper;
import com.memplas.parking.feature.paymentprocessing.model.Payment;
import com.memplas.parking.feature.paymentprocessing.model.PaymentMethod;
import com.memplas.parking.feature.paymentprocessing.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final ParkingSessionRepository sessionRepository;

    private final PaymentMapper paymentMapper;

    private final List<PaymentStrategy> paymentStrategies;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public PaymentService(
            PaymentRepository paymentRepository,
            ParkingSessionRepository sessionRepository,
            PaymentMapper paymentMapper,
            List<PaymentStrategy> paymentStrategies,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.paymentRepository = paymentRepository;
        this.sessionRepository = sessionRepository;
        this.paymentMapper = paymentMapper;
        this.paymentStrategies = paymentStrategies;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    /**
     * Performance: Strategy pattern enables clean separation and easy testing
     * Each payment method has optimized implementation for its provider
     */
    public PaymentDto processPayment(PaymentRequestDto request) {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Verify user owns the session they're paying for
        ParkingSession session = sessionRepository.findBySessionReference(request.sessionReference())
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + request.sessionReference()));

        if (!hasRole("ADMIN") && !currentUser.userId().equals(session.getUser().getId())) {
            throw new SecurityException("Access denied: Cannot pay for other user's session");
        }

        PaymentStrategy strategy = findStrategy(request.paymentMethod(), request.paymentProvider());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method or provider");
        }

        Payment payment = strategy.processPayment(request, request.sessionReference());
        return paymentMapper.toDto(payment);
    }

    // Public endpoint: Payment provider callbacks don't have user context
    public void handlePaymentCallback(PaymentCallbackDto callback) {
        // Performance: Find strategy based on payment reference lookup
        Payment payment = paymentRepository.findByPaymentReference(callback.reference())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + callback.reference()));

        PaymentStrategy strategy = findStrategy(payment.getPaymentMethod(), payment.getPaymentProvider());
        if (strategy != null) {
            strategy.handleCallback(callback);
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> getCurrentUserPayments() {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Join query to get payments for user's sessions
        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getSession().getUser().getId().equals(currentUser.userId()))
                .toList();

        return payments.stream().map(paymentMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Users can only view their own payments (unless admin)
        if (!hasRole("ADMIN") && !currentUser.userId().equals(payment.getSession().getUser().getId())) {
            throw new SecurityException("Access denied: Cannot view other user's payment");
        }

        return paymentMapper.toDto(payment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<PaymentDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(paymentMapper::toDto).toList();
    }

    private PaymentStrategy findStrategy(PaymentMethod method, String provider) {
        return paymentStrategies.stream()
                .filter(strategy -> strategy.supports(provider))
                .findFirst()
                .orElse(null);
    }

    private boolean hasRole(String role) {
        return authenticatedUserProvider.getCurrentKeycloakUser().roles().contains(role);
    }
}
