package com.memplas.parking.feature.facility.model;

import com.memplas.parking.core.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_facility_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"facility_id", "date"}))
public class DailyFacilityStats extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    @NotNull(message = "Facility is required")
    private ParkingFacility facility;

    @Column(nullable = false)
    @NotNull(message = "Date is required")
    private LocalDate date;

    @Column(nullable = false)
    private Integer totalSessions = 0;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer avgSessionDurationMinutes = 0;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peakOccupancyRate = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer violationsCount = 0;

    // Constructors
    public DailyFacilityStats() {}

    public DailyFacilityStats(Long id) {
        super(id);
    }

    // Getters and Setters
    public ParkingFacility getFacility() {return facility;}

    public void setFacility(ParkingFacility facility) {this.facility = facility;}

    public LocalDate getDate() {return date;}

    public void setDate(LocalDate date) {this.date = date;}

    public Integer getTotalSessions() {return totalSessions;}

    public void setTotalSessions(Integer totalSessions) {this.totalSessions = totalSessions;}

    public BigDecimal getTotalRevenue() {return totalRevenue;}

    public void setTotalRevenue(BigDecimal totalRevenue) {this.totalRevenue = totalRevenue;}

    public Integer getAvgSessionDurationMinutes() {return avgSessionDurationMinutes;}

    public void setAvgSessionDurationMinutes(Integer avgSessionDurationMinutes) {this.avgSessionDurationMinutes = avgSessionDurationMinutes;}

    public BigDecimal getPeakOccupancyRate() {return peakOccupancyRate;}

    public void setPeakOccupancyRate(BigDecimal peakOccupancyRate) {this.peakOccupancyRate = peakOccupancyRate;}

    public Integer getViolationsCount() {return violationsCount;}

    public void setViolationsCount(Integer violationsCount) {this.violationsCount = violationsCount;}
}
