package com.memplas.parking.feature.parkingsession.model;

import com.memplas.parking.core.model.BaseEntity;
import com.memplas.parking.feature.book.model.User;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
import com.memplas.parking.feature.parkingviolation.model.Violation;
import com.memplas.parking.feature.paymentprocessing.model.Payment;
import com.memplas.parking.feature.vehicle.model.Vehicle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

//TODO ONE CAR RESERVE AT A TIME
@Entity
@Table(name = "parking_sessions")
public class ParkingSession extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Session reference is required")
    @Size(max = 50, message = "Session reference must not exceed 50 characters")
    private String sessionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    @NotNull(message = "Parking spot is required")
    private ParkingSpot spot;

    @Column(nullable = false)
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer plannedDurationMinutes;

    private Integer actualDurationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.RESERVED;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String currency = "TZS";

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Payment> payments = new HashSet<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Violation> violations = new HashSet<>();

    // Constructors
    public ParkingSession() {}

    public ParkingSession(Long id) {
        super(id);
    }

    // Getters and Setters
    public String getSessionReference() {return sessionReference;}

    public void setSessionReference(String sessionReference) {this.sessionReference = sessionReference;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

    public Vehicle getVehicle() {return vehicle;}

    public void setVehicle(Vehicle vehicle) {this.vehicle = vehicle;}

    public ParkingSpot getSpot() {return spot;}

    public void setSpot(ParkingSpot spot) {this.spot = spot;}

    public LocalDateTime getStartTime() {return startTime;}

    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    public LocalDateTime getEndTime() {return endTime;}

    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}

    public Integer getPlannedDurationMinutes() {return plannedDurationMinutes;}

    public void setPlannedDurationMinutes(Integer plannedDurationMinutes) {this.plannedDurationMinutes = plannedDurationMinutes;}

    public Integer getActualDurationMinutes() {return actualDurationMinutes;}

    public void setActualDurationMinutes(Integer actualDurationMinutes) {this.actualDurationMinutes = actualDurationMinutes;}

    public SessionStatus getStatus() {return status;}

    public void setStatus(SessionStatus status) {this.status = status;}

    public BigDecimal getTotalAmount() {return totalAmount;}

    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}

    public String getCurrency() {return currency;}

    public void setCurrency(String currency) {this.currency = currency;}

    public Set<Payment> getPayments() {return payments;}

    public void setPayments(Set<Payment> payments) {this.payments = payments;}

    public Set<Violation> getViolations() {return violations;}

    public void setViolations(Set<Violation> violations) {this.violations = violations;}
}
