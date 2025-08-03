package com.memplas.parking.feature.parkingsession.service;

import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import com.memplas.parking.feature.book.model.User;
import com.memplas.parking.feature.parkingsession.dto.ParkingSessionDto;
import com.memplas.parking.feature.parkingsession.dto.ReservationRequestDto;
import com.memplas.parking.feature.parkingsession.dto.ReservationResponseDto;
import com.memplas.parking.feature.parkingsession.mapper.ParkingSessionMapper;
import com.memplas.parking.feature.parkingsession.mapper.ReservationMapper;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import com.memplas.parking.feature.parkingsession.model.SessionStatus;
import com.memplas.parking.feature.parkingsession.repository.ParkingSessionRepository;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
import com.memplas.parking.feature.parkingspot.model.SpotStatus;
import com.memplas.parking.feature.parkingspot.repository.ParkingSpotRepository;
import com.memplas.parking.feature.parkingspot.service.AvailabilityCacheService;
import com.memplas.parking.feature.pricing.service.PricingRuleService;
import com.memplas.parking.feature.vehicle.model.Vehicle;
import com.memplas.parking.feature.vehicle.service.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class ParkingSessionService {
    private final ParkingSessionRepository sessionRepository;

    private final ParkingSpotRepository spotRepository;

    private final ParkingSessionMapper sessionMapper;

    private final ReservationMapper reservationMapper;

    private final PricingRuleService pricingRuleService;

    private final VehicleService vehicleService;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public ParkingSessionService(
            ParkingSessionRepository sessionRepository,
            ParkingSpotRepository spotRepository,
            ParkingSessionMapper sessionMapper,
            ReservationMapper reservationMapper, PricingRuleService pricingRuleService,
            AvailabilityCacheService cacheService, VehicleService vehicleService,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.sessionRepository = sessionRepository;
        this.spotRepository = spotRepository;
        this.sessionMapper = sessionMapper;
        this.reservationMapper = reservationMapper;
        this.pricingRuleService = pricingRuleService;
        this.vehicleService = vehicleService;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    /**
     * Critical Performance Method: Handles high-concurrency spot reservations
     * Uses optimistic locking and retry mechanism to handle race conditions
     * Isolation level READ_COMMITTED prevents phantom reads while allowing concurrent access
     */
    @Retryable(
            retryFor = {OptimisticLockException.class, DataAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )

    //TODO USE LESS GENERIC EXCEPTIONS

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationResponseDto reserveSpot(ReservationRequestDto request) {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        ParkingSpot spot = spotRepository.findById(request.spotId())
                .orElseThrow(() -> new EntityNotFoundException("Spot not found"));

        if (spot.getStatus() != SpotStatus.AVAILABLE) {
            throw new RuntimeException("Spot is not available");
        }
        boolean exists = sessionRepository.existsByVehicleIdAndStatusIn(request.vehicleId(), List.of(SessionStatus.ACTIVE, SessionStatus.RESERVED));

//        if (exists) {//TODO CHECK AGAIN
//            throw new RuntimeException("Vehicle already has an active or reserved parking session");
//        }
        var vehicle = vehicleService.getVehicleById(request.vehicleId());
        String sessionRef = generateSessionReference();
        int minutes = request.plannedDurationMinutes();
        int hours = (int) Math.ceil(minutes / 60.0);

        var estimatedAmount = pricingRuleService.calculatePricing(
                spot.getFacility().getId(), spot.getSpotType(), vehicle.vehicleType(),

                spot.getFloorLevel(), hours, LocalDateTime.now());

        // Critical: Atomic spot reservation update
        spot.setStatus(SpotStatus.RESERVED);
        spot.setReservationExpiry(LocalDateTime.now().plusMinutes(15));
        spot.setReservedBy(new User(currentUser.userId2));
        spotRepository.save(spot);

        // Create parking session
        ParkingSession session = new ParkingSession();
        session.setSessionReference(sessionRef);
        session.setUser(new User(currentUser.userId2));
        session.setVehicle(new Vehicle(request.vehicleId()));
        session.setSpot(spot);
        session.setStartTime(request.startTime());
        session.setPlannedDurationMinutes(request.plannedDurationMinutes());
        session.setStatus(SessionStatus.RESERVED);
        session.setTotalAmount(estimatedAmount);

        ParkingSession savedSession = sessionRepository.save(session);


        return reservationMapper.toReservationResponse(savedSession);
    }

    /**
     * Performance: Optimized session reference generation
     * Avoids DB lookups by using timestamp + random combination
     */
    private String generateSessionReference() {
        long timestamp = System.currentTimeMillis() % 1000000; // Last 6 digits
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return String.format("PACK-%06d", timestamp + random);
    }

    @Transactional(readOnly = true)
    public ParkingSessionDto getSessionByReference(String sessionReference) {
        ParkingSession session = sessionRepository.findBySessionReference(sessionReference)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionReference));

        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Users can only view their own sessions (unless admin/enforcement)
        if (!hasRole("ADMIN") && !hasRole("ENFORCEMENT_OFFICER") &&
                !currentUser.userId2.equals(session.getUser().getId())) {
            throw new SecurityException("Access denied: Cannot view other user's session");
        }

        return sessionMapper.toDto(session);
    }

    public ParkingSessionDto confirmArrival(String sessionReference) {
        ParkingSession session = sessionRepository.findBySessionReference(sessionReference)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionReference));

        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Users can only confirm their own sessions
        if (!currentUser.userId2.equals(session.getUser().getId())) {
            throw new SecurityException("Access denied: Cannot confirm other user's session");
        }

        if (session.getStatus() != SessionStatus.RESERVED) {
            throw new IllegalStateException("Session is not in reserved state");
        }

        // Update session status
        session.setStatus(SessionStatus.ACTIVE);
        session.setStartTime(LocalDateTime.now());

        // Update spot status
        ParkingSpot spot = session.getSpot();
        spot.setStatus(SpotStatus.OCCUPIED);
        spot.setReservationExpiry(null);
        spot.setLastOccupiedAt(LocalDateTime.now());

        ParkingSession savedSession = sessionRepository.save(session);

        return sessionMapper.toDto(savedSession);
    }

    @Transactional(readOnly = true)
    public List<ParkingSessionDto> getCurrentUserActiveSessions() {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();
        List<SessionStatus> activeStatuses = List.of(SessionStatus.RESERVED, SessionStatus.ACTIVE);
        List<ParkingSession> sessions = sessionRepository.findByUserIdAndStatus(currentUser.userId2, SessionStatus.ACTIVE);
        return sessions.stream().map(sessionMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ParkingSessionDto> getUserSessions(Long userId) {
        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Users can only view their own sessions (unless admin)
        if (!hasRole("ADMIN") && !currentUser.userId2.equals(userId)) {
            throw new SecurityException("Access denied: Cannot view other user's sessions");
        }

        List<ParkingSession> sessions = sessionRepository.findByUserId(userId);
        return sessions.stream().map(sessionMapper::toDto).toList();
    }

    public ParkingSessionDto endSession(String sessionReference) {
        ParkingSession session = sessionRepository.findBySessionReference(sessionReference)
                .orElseThrow(() -> new EntityNotFoundException("Session not found: " + sessionReference));

        var currentUser = authenticatedUserProvider.getCurrentKeycloakUser();

        // Security: Users can only end their own sessions (unless admin/enforcement)
        if (!hasRole("ADMIN") && !hasRole("ENFORCEMENT_OFFICER") &&
                !currentUser.userId2.equals(session.getUser().getId())) {
            throw new SecurityException("Access denied: Cannot end other user's session");
        }

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new IllegalStateException("Session is not active");
        }

        // Calculate actual duration and final amount
        LocalDateTime endTime = LocalDateTime.now();
        session.setEndTime(endTime);
        session.setStatus(SessionStatus.COMPLETED);

        long durationMinutes = java.time.Duration.between(session.getStartTime(), endTime).toMinutes();
        session.setActualDurationMinutes((int) durationMinutes);
        BigDecimal totalAmount = session.getTotalAmount();
        long plannedMinutes = session.getPlannedDurationMinutes();

        BigDecimal finalAmount = totalAmount
                .multiply(BigDecimal.valueOf(durationMinutes))
                .divide(BigDecimal.valueOf(plannedMinutes), 2, RoundingMode.HALF_UP);
        session.setTotalAmount(finalAmount);

        // Release spot
        ParkingSpot spot = session.getSpot();
        spot.setStatus(SpotStatus.AVAILABLE);
        spot.setReservedBy(null);

        ParkingSession savedSession = sessionRepository.save(session);

        return sessionMapper.toDto(savedSession);
    }

    private boolean hasRole(String role) {
        return authenticatedUserProvider.getCurrentKeycloakUser().roles().contains(role);
    }
}
