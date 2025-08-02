package com.memplas.parking.feature.parkingspot.model;

import com.memplas.parking.core.model.BaseEntity;
import com.memplas.parking.feature.book.model.User;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import com.memplas.parking.feature.parkingviolation.model.Violation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parking_spots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"facility_id", "spotNumber"}))
public class ParkingSpot extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    @NotNull(message = "Facility is required")
    private ParkingFacility facility;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Spot number is required")
    @Size(max = 20, message = "Spot number must not exceed 20 characters")
    private String spotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotType spotType = SpotType.REGULAR;

    @Column(nullable = false)
    private Integer floorLevel = 0;

    @Column(length = 50)
    @Size(max = 50, message = "Zone must not exceed 50 characters")
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotStatus status = SpotStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_by")
    private User reservedBy;

    private LocalDateTime reservationExpiry;

    private LocalDateTime lastOccupiedAt;

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ParkingSession> parkingSessions = new HashSet<>();

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Violation> violations = new HashSet<>();

    // Constructors
    public ParkingSpot() {}

    public ParkingSpot(Long id) {
        super(id);
    }

    // Getters and Setters
    public ParkingFacility getFacility() {return facility;}

    public void setFacility(ParkingFacility facility) {this.facility = facility;}

    public String getSpotNumber() {return spotNumber;}

    public void setSpotNumber(String spotNumber) {this.spotNumber = spotNumber;}

    public SpotType getSpotType() {return spotType;}

    public void setSpotType(SpotType spotType) {this.spotType = spotType;}

    public Integer getFloorLevel() {return floorLevel;}

    public void setFloorLevel(Integer floorLevel) {this.floorLevel = floorLevel;}

    public String getZone() {return zone;}

    public void setZone(String zone) {this.zone = zone;}

    public SpotStatus getStatus() {return status;}

    public void setStatus(SpotStatus status) {this.status = status;}

    public User getReservedBy() {return reservedBy;}

    public void setReservedBy(User reservedBy) {this.reservedBy = reservedBy;}

    public LocalDateTime getReservationExpiry() {return reservationExpiry;}

    public void setReservationExpiry(LocalDateTime reservationExpiry) {this.reservationExpiry = reservationExpiry;}

    public LocalDateTime getLastOccupiedAt() {return lastOccupiedAt;}

    public void setLastOccupiedAt(LocalDateTime lastOccupiedAt) {this.lastOccupiedAt = lastOccupiedAt;}

    public Set<ParkingSession> getParkingSessions() {return parkingSessions;}

    public void setParkingSessions(Set<ParkingSession> parkingSessions) {this.parkingSessions = parkingSessions;}

    public Set<Violation> getViolations() {return violations;}

    public void setViolations(Set<Violation> violations) {this.violations = violations;}
}
