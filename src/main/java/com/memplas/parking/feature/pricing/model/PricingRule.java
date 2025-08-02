package com.memplas.parking.feature.pricing.model;

import com.memplas.parking.core.model.BaseEntity;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "pricing_rules")
public class PricingRule extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    @NotNull(message = "Facility is required")
    private ParkingFacility facility;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Rule name is required")
    @Size(max = 100, message = "Rule name must not exceed 100 characters")
    private String ruleName;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Base rate is required")
    private BigDecimal baseRate;

    @Column(nullable = false, length = 3)
    private String currency = "TZS";

    private LocalTime timeOfDayStart;

    private LocalTime timeOfDayEnd;

    @Column(length = 7)
    private String dayOfWeekMask = "1111111"; // MTWTFSS bitmap

    @Column(precision = 3, scale = 2)
    private BigDecimal demandMultiplier = BigDecimal.valueOf(1.00);

    @Column(nullable = false)
    private Integer occupancyThreshold = 80;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDailyRate;

    @Column(nullable = false)
    private Integer gracePeriodMinutes = 15;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    @NotNull(message = "Effective from date is required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveUntil;

    // Constructors
    public PricingRule() {}

    public PricingRule(Long id) {
        super(id);
    }

    // Getters and Setters
    public ParkingFacility getFacility() {return facility;}

    public void setFacility(ParkingFacility facility) {this.facility = facility;}

    public String getRuleName() {return ruleName;}

    public void setRuleName(String ruleName) {this.ruleName = ruleName;}

    public BigDecimal getBaseRate() {return baseRate;}

    public void setBaseRate(BigDecimal baseRate) {this.baseRate = baseRate;}

    public String getCurrency() {return currency;}

    public void setCurrency(String currency) {this.currency = currency;}

    public LocalTime getTimeOfDayStart() {return timeOfDayStart;}

    public void setTimeOfDayStart(LocalTime timeOfDayStart) {this.timeOfDayStart = timeOfDayStart;}

    public LocalTime getTimeOfDayEnd() {return timeOfDayEnd;}

    public void setTimeOfDayEnd(LocalTime timeOfDayEnd) {this.timeOfDayEnd = timeOfDayEnd;}

    public String getDayOfWeekMask() {return dayOfWeekMask;}

    public void setDayOfWeekMask(String dayOfWeekMask) {this.dayOfWeekMask = dayOfWeekMask;}

    public BigDecimal getDemandMultiplier() {return demandMultiplier;}

    public void setDemandMultiplier(BigDecimal demandMultiplier) {this.demandMultiplier = demandMultiplier;}

    public Integer getOccupancyThreshold() {return occupancyThreshold;}

    public void setOccupancyThreshold(Integer occupancyThreshold) {this.occupancyThreshold = occupancyThreshold;}

    public BigDecimal getMaxDailyRate() {return maxDailyRate;}

    public void setMaxDailyRate(BigDecimal maxDailyRate) {this.maxDailyRate = maxDailyRate;}

    public Integer getGracePeriodMinutes() {return gracePeriodMinutes;}

    public void setGracePeriodMinutes(Integer gracePeriodMinutes) {this.gracePeriodMinutes = gracePeriodMinutes;}

    public Boolean getIsActive() {return isActive;}

    public void setIsActive(Boolean isActive) {this.isActive = isActive;}

    public LocalDate getEffectiveFrom() {return effectiveFrom;}

    public void setEffectiveFrom(LocalDate effectiveFrom) {this.effectiveFrom = effectiveFrom;}

    public LocalDate getEffectiveUntil() {return effectiveUntil;}

    public void setEffectiveUntil(LocalDate effectiveUntil) {this.effectiveUntil = effectiveUntil;}
}
