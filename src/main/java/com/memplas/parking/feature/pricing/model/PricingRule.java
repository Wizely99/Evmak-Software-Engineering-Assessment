package com.memplas.parking.feature.pricing.model;

import com.memplas.parking.core.model.BaseEntity;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import com.memplas.parking.feature.parkingspot.model.SpotType;
import com.memplas.parking.feature.vehicle.model.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "pricing_rules")
public class PricingRule extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false, unique = true)
    @NotNull(message = "Facility is required")
    private ParkingFacility facility;

    // Base rates
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Base rate is required")
    private BigDecimal baseRate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal vipRate; // Premium for VIP spots

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal evChargingRate; // Rate for EV charging spots

    @Column(nullable = false, length = 3)
    private String currency = "TZS";

    // Time-based pricing
    @Column(precision = 3, scale = 2)
    private BigDecimal peakHourMultiplier = BigDecimal.valueOf(1.50); // 6-9AM, 5-8PM

    @Column(precision = 3, scale = 2)
    private BigDecimal offPeakMultiplier = BigDecimal.valueOf(0.80); // 10PM-6AM

    // Floor-based discounts (higher floors = cheaper)
    @Column(precision = 5, scale = 2)
    private BigDecimal floorDiscountPerLevel = BigDecimal.valueOf(5.00); // 5% discount per floor above ground

    @Column
    private Integer maxFloorDiscount = 30; // Max 30% discount for highest floors

    // Long-term parking
    @Column
    private Integer discountAfterHours = 4; // Start discount after 4 hours

    @Column(precision = 5, scale = 2)
    private BigDecimal hourlyDiscountRate = BigDecimal.valueOf(2.00); // 2% per additional hour

    @Column(precision = 5, scale = 2)
    private BigDecimal maxDiscountPercentage = BigDecimal.valueOf(30.00); // Max 30% discount

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDailyRate;

    // Demand-based pricing
    @Column(precision = 3, scale = 2)
    private BigDecimal demandMultiplier = BigDecimal.valueOf(1.00);

    @Column(nullable = false)
    private Integer occupancyThreshold = 80;

    // Weather conditions
    @Column(precision = 3, scale = 2)
    private BigDecimal weatherMultiplierRain = BigDecimal.valueOf(1.20);

    @Column(precision = 3, scale = 2)
    private BigDecimal weatherMultiplierSnow = BigDecimal.valueOf(1.50);

    @Column(precision = 3, scale = 2)
    private BigDecimal weatherMultiplierExtremeHeat = BigDecimal.valueOf(1.30);

    // Event pricing
    @Column(precision = 3, scale = 2)
    private BigDecimal eventMultiplier = BigDecimal.valueOf(1.25);

    // Vehicle type multipliers
    @Column(precision = 3, scale = 2)
    private BigDecimal motorcycleDiscount = BigDecimal.valueOf(0.70); // 30% discount

    @Column(precision = 3, scale = 2)
    private BigDecimal truckSurcharge = BigDecimal.valueOf(1.50); // 50% surcharge

    // Grace period and validation
    @Column(nullable = false)
    private Integer gracePeriodMinutes = 15;

    // Calculate comprehensive rate
    //TODO :THIS SHOULD BE MOVED TO UTIL CLASS
    public BigDecimal calculateRate(SpotType spotType, VehicleType vehicleType, int floorLevel,
                                    int hours, WeatherCondition weather, boolean isEvent,
                                    boolean isPeakHour, boolean isOffPeak) {
        // Start with base rate based on spot type
        BigDecimal rate = getBaseRateForSpotType(spotType);

        // Apply vehicle type multiplier
        rate = rate.multiply(getVehicleTypeMultiplier(vehicleType));

        // Apply time multipliers
        if (isPeakHour) {
            rate = rate.multiply(peakHourMultiplier);
        } else if (isOffPeak) {
            rate = rate.multiply(offPeakMultiplier);
        }

        // Apply floor discount (higher floors cheaper)
        if (floorLevel > 0) {
            BigDecimal floorDiscount = floorDiscountPerLevel.multiply(BigDecimal.valueOf(floorLevel));
            if (floorDiscount.compareTo(BigDecimal.valueOf(maxFloorDiscount)) > 0) {
                floorDiscount = BigDecimal.valueOf(maxFloorDiscount);
            }
            rate = rate.multiply(BigDecimal.ONE.subtract(floorDiscount.divide(BigDecimal.valueOf(100))));
        }

        // Calculate for total hours
        rate = rate.multiply(BigDecimal.valueOf(hours));

        // Apply weather multiplier
        if (weather != null) {
            rate = rate.multiply(getWeatherMultiplier(weather));
        }

        // Apply event multiplier
        if (isEvent) {
            rate = rate.multiply(eventMultiplier);
        }

        // Apply demand multiplier
        rate = rate.multiply(demandMultiplier);

        // Apply long-term discount
        if (hours > discountAfterHours) {
            BigDecimal discount = calculateLongTermDiscount(hours);
            rate = rate.multiply(BigDecimal.ONE.subtract(discount));
        }

        // Apply daily cap
        if (maxDailyRate != null && rate.compareTo(maxDailyRate) > 0) {
            rate = maxDailyRate;
        }

        return rate;
    }

    private BigDecimal getBaseRateForSpotType(SpotType spotType) {
        return switch (spotType) {
            case VIP -> vipRate;
            case EV_CHARGING -> evChargingRate;
            default -> baseRate;
        };
    }

    private BigDecimal getVehicleTypeMultiplier(VehicleType vehicleType) {
        return switch (vehicleType) {
            case MOTORCYCLE -> motorcycleDiscount;
            case TRUCK -> truckSurcharge;
            case CAR, VAN -> BigDecimal.ONE; // Base rate
        };
    }

    private BigDecimal getWeatherMultiplier(WeatherCondition weather) {
        return switch (weather) {
            case RAIN -> weatherMultiplierRain;
            case SNOW -> weatherMultiplierSnow;
            case EXTREME_HEAT -> weatherMultiplierExtremeHeat;
            default -> BigDecimal.ONE;
        };
    }

    private BigDecimal calculateLongTermDiscount(int hours) {
        int extraHours = hours - discountAfterHours;
        BigDecimal discount = hourlyDiscountRate.multiply(BigDecimal.valueOf(extraHours));

        if (discount.compareTo(maxDiscountPercentage) > 0) {
            discount = maxDiscountPercentage;
        }

        return discount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public ParkingFacility getFacility() {
        return facility;
    }

    public void setFacility(ParkingFacility facility) {
        this.facility = facility;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    public BigDecimal getVipRate() {
        return vipRate;
    }

    public void setVipRate(BigDecimal vipRate) {
        this.vipRate = vipRate;
    }

    public BigDecimal getEvChargingRate() {
        return evChargingRate;
    }

    public void setEvChargingRate(BigDecimal evChargingRate) {
        this.evChargingRate = evChargingRate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPeakHourMultiplier() {
        return peakHourMultiplier;
    }

    public void setPeakHourMultiplier(BigDecimal peakHourMultiplier) {
        this.peakHourMultiplier = peakHourMultiplier;
    }

    public BigDecimal getOffPeakMultiplier() {
        return offPeakMultiplier;
    }

    public void setOffPeakMultiplier(BigDecimal offPeakMultiplier) {
        this.offPeakMultiplier = offPeakMultiplier;
    }

    public BigDecimal getFloorDiscountPerLevel() {
        return floorDiscountPerLevel;
    }

    public void setFloorDiscountPerLevel(BigDecimal floorDiscountPerLevel) {
        this.floorDiscountPerLevel = floorDiscountPerLevel;
    }

    public Integer getMaxFloorDiscount() {
        return maxFloorDiscount;
    }

    public void setMaxFloorDiscount(Integer maxFloorDiscount) {
        this.maxFloorDiscount = maxFloorDiscount;
    }

    public Integer getDiscountAfterHours() {
        return discountAfterHours;
    }

    public void setDiscountAfterHours(Integer discountAfterHours) {
        this.discountAfterHours = discountAfterHours;
    }

    public BigDecimal getHourlyDiscountRate() {
        return hourlyDiscountRate;
    }

    public void setHourlyDiscountRate(BigDecimal hourlyDiscountRate) {
        this.hourlyDiscountRate = hourlyDiscountRate;
    }

    public BigDecimal getMaxDiscountPercentage() {
        return maxDiscountPercentage;
    }

    public void setMaxDiscountPercentage(BigDecimal maxDiscountPercentage) {
        this.maxDiscountPercentage = maxDiscountPercentage;
    }

    public BigDecimal getMaxDailyRate() {
        return maxDailyRate;
    }

    public void setMaxDailyRate(BigDecimal maxDailyRate) {
        this.maxDailyRate = maxDailyRate;
    }

    public BigDecimal getDemandMultiplier() {
        return demandMultiplier;
    }

    public void setDemandMultiplier(BigDecimal demandMultiplier) {
        this.demandMultiplier = demandMultiplier;
    }

    public Integer getOccupancyThreshold() {
        return occupancyThreshold;
    }

    public void setOccupancyThreshold(Integer occupancyThreshold) {
        this.occupancyThreshold = occupancyThreshold;
    }

    public BigDecimal getWeatherMultiplierRain() {
        return weatherMultiplierRain;
    }

    public void setWeatherMultiplierRain(BigDecimal weatherMultiplierRain) {
        this.weatherMultiplierRain = weatherMultiplierRain;
    }

    public BigDecimal getWeatherMultiplierSnow() {
        return weatherMultiplierSnow;
    }

    public void setWeatherMultiplierSnow(BigDecimal weatherMultiplierSnow) {
        this.weatherMultiplierSnow = weatherMultiplierSnow;
    }

    public BigDecimal getWeatherMultiplierExtremeHeat() {
        return weatherMultiplierExtremeHeat;
    }

    public void setWeatherMultiplierExtremeHeat(BigDecimal weatherMultiplierExtremeHeat) {
        this.weatherMultiplierExtremeHeat = weatherMultiplierExtremeHeat;
    }

    public BigDecimal getEventMultiplier() {
        return eventMultiplier;
    }

    public void setEventMultiplier(BigDecimal eventMultiplier) {
        this.eventMultiplier = eventMultiplier;
    }

    public BigDecimal getMotorcycleDiscount() {
        return motorcycleDiscount;
    }

    public void setMotorcycleDiscount(BigDecimal motorcycleDiscount) {
        this.motorcycleDiscount = motorcycleDiscount;
    }

    public BigDecimal getTruckSurcharge() {
        return truckSurcharge;
    }

    public void setTruckSurcharge(BigDecimal truckSurcharge) {
        this.truckSurcharge = truckSurcharge;
    }

    public Integer getGracePeriodMinutes() {
        return gracePeriodMinutes;
    }

    public void setGracePeriodMinutes(Integer gracePeriodMinutes) {
        this.gracePeriodMinutes = gracePeriodMinutes;
    }

}



