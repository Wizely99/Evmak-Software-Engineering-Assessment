package com.memplas.parking.feature.facility.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facility_availability_cache")
public class FacilityAvailabilityCache {
    @Id
    private Long facilityId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "facility_id")
    private ParkingFacility facility;

    @Column(nullable = false)
    @NotNull(message = "Total spots is required")
    private Integer totalSpots;

    @Column(nullable = false)
    @NotNull(message = "Available spots is required")
    private Integer availableSpots;

    @Column(nullable = false)
    @NotNull(message = "Occupied spots is required")
    private Integer occupiedSpots;

    @Column(nullable = false)
    @NotNull(message = "Reserved spots is required")
    private Integer reservedSpots;

    @Column(nullable = false)
    @NotNull(message = "Out of order spots is required")
    private Integer outOfOrderSpots;

    @Column(nullable = false, precision = 5, scale = 2)
    @NotNull(message = "Current occupancy rate is required")
    private BigDecimal currentOccupancyRate;

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    // Constructors
    public FacilityAvailabilityCache() {}

    public FacilityAvailabilityCache(Long facilityId) {
        this.facilityId = facilityId;
    }

    // Getters and Setters
    public Long getFacilityId() {return facilityId;}

    public void setFacilityId(Long facilityId) {this.facilityId = facilityId;}

    public ParkingFacility getFacility() {return facility;}

    public void setFacility(ParkingFacility facility) {this.facility = facility;}

    public Integer getTotalSpots() {return totalSpots;}

    public void setTotalSpots(Integer totalSpots) {this.totalSpots = totalSpots;}

    public Integer getAvailableSpots() {return availableSpots;}

    public void setAvailableSpots(Integer availableSpots) {this.availableSpots = availableSpots;}

    public Integer getOccupiedSpots() {return occupiedSpots;}

    public void setOccupiedSpots(Integer occupiedSpots) {this.occupiedSpots = occupiedSpots;}

    public Integer getReservedSpots() {return reservedSpots;}

    public void setReservedSpots(Integer reservedSpots) {this.reservedSpots = reservedSpots;}

    public Integer getOutOfOrderSpots() {return outOfOrderSpots;}

    public void setOutOfOrderSpots(Integer outOfOrderSpots) {this.outOfOrderSpots = outOfOrderSpots;}

    public BigDecimal getCurrentOccupancyRate() {return currentOccupancyRate;}

    public void setCurrentOccupancyRate(BigDecimal currentOccupancyRate) {this.currentOccupancyRate = currentOccupancyRate;}

    public LocalDateTime getLastUpdated() {return lastUpdated;}

    public void setLastUpdated(LocalDateTime lastUpdated) {this.lastUpdated = lastUpdated;}
}
