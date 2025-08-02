package com.memplas.parking.feature.parkingviolation.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.memplas.parking.core.model.BaseEntity;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
import com.memplas.parking.feature.vehicle.model.Vehicle;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "violations")
public class Violation extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private ParkingSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    @NotNull(message = "Spot is required")
    private ParkingSpot spot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Violation type is required")
    private ViolationType violationType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Fine amount is required")
    private BigDecimal fineAmount;

    @Column(nullable = false, length = 3)
    private String currency = "TZS";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViolationStatus status = ViolationStatus.PENDING;

    @Column(nullable = false)
    @NotNull(message = "Detection time is required")
    private LocalDateTime detectedAt;

    @Column(length = 100)
    private String reportedBy;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode evidenceUrls;

    private LocalDateTime resolvedAt;

    // Constructors
    public Violation() {}

    public Violation(Long id) {
        super(id);
    }

    // Getters and Setters
    public ParkingSession getSession() {return session;}

    public void setSession(ParkingSession session) {this.session = session;}

    public Vehicle getVehicle() {return vehicle;}

    public void setVehicle(Vehicle vehicle) {this.vehicle = vehicle;}

    public ParkingSpot getSpot() {return spot;}

    public void setSpot(ParkingSpot spot) {this.spot = spot;}

    public ViolationType getViolationType() {return violationType;}

    public void setViolationType(ViolationType violationType) {this.violationType = violationType;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public BigDecimal getFineAmount() {return fineAmount;}

    public void setFineAmount(BigDecimal fineAmount) {this.fineAmount = fineAmount;}

    public String getCurrency() {return currency;}

    public void setCurrency(String currency) {this.currency = currency;}

    public ViolationStatus getStatus() {return status;}

    public void setStatus(ViolationStatus status) {this.status = status;}

    public LocalDateTime getDetectedAt() {return detectedAt;}

    public void setDetectedAt(LocalDateTime detectedAt) {this.detectedAt = detectedAt;}

    public String getReportedBy() {return reportedBy;}

    public void setReportedBy(String reportedBy) {this.reportedBy = reportedBy;}

    public JsonNode getEvidenceUrls() {return evidenceUrls;}

    public void setEvidenceUrls(JsonNode evidenceUrls) {this.evidenceUrls = evidenceUrls;}

    public LocalDateTime getResolvedAt() {return resolvedAt;}

    public void setResolvedAt(LocalDateTime resolvedAt) {this.resolvedAt = resolvedAt;}
}
