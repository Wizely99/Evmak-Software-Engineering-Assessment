package com.memplas.parking.feature.pricing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Pricing rule configuration for a parking facility")
public record PricingRuleDto(
        @Schema(description = "Pricing rule ID", example = "1")
        Long id,
        @NotNull(message = "Facility ID is required")
        @Positive(message = "Facility ID must be positive")
        @Schema(description = "Facility ID", example = "1", required = true)
        Long facilityId,
        @NotBlank(message = "Rule name is required")
        @Size(max = 100, message = "Rule name must not exceed 100 characters")
        @Schema(description = "Name of the pricing rule", example = "Downtown Peak Hours", required = true)
        String ruleName,

        // Base rates
        @NotNull(message = "Base rate is required")
        @Positive(message = "Base rate must be positive")
        @Schema(description = "Base hourly rate", example = "2000.00", required = true)
        BigDecimal baseRate,
        @NotNull(message = "VIP rate is required")
        @Positive(message = "VIP rate must be positive")
        @Schema(description = "VIP spot hourly rate", example = "3000.00", required = true)
        BigDecimal vipRate,
        @NotNull(message = "EV charging rate is required")
        @Positive(message = "EV charging rate must be positive")
        @Schema(description = "EV charging spot hourly rate", example = "2500.00", required = true)
        BigDecimal evChargingRate,
        @Size(max = 3, message = "Currency must be 3 characters")
        @Schema(description = "Currency code", example = "TZS")
        String currency,

        // Time-based multipliers
        @DecimalMin(value = "0.1", message = "Peak hour multiplier must be at least 0.1")
        @DecimalMax(value = "5.0", message = "Peak hour multiplier must not exceed 5.0")
        @Schema(description = "Peak hour price multiplier", example = "1.50")
        BigDecimal peakHourMultiplier,
        @DecimalMin(value = "0.1", message = "Off-peak multiplier must be at least 0.1")
        @DecimalMax(value = "5.0", message = "Off-peak multiplier must not exceed 5.0")
        @Schema(description = "Off-peak hour price multiplier", example = "0.80")
        BigDecimal offPeakMultiplier,

        // Floor-based discounts
        @DecimalMin(value = "0.0", message = "Floor discount must be non-negative")
        @DecimalMax(value = "50.0", message = "Floor discount per level must not exceed 50%")
        @Schema(description = "Discount percentage per floor above ground", example = "5.00")
        BigDecimal floorDiscountPerLevel,
        @Min(value = 0, message = "Max floor discount must be non-negative")
        @Max(value = 90, message = "Max floor discount must not exceed 90%")
        @Schema(description = "Maximum floor discount percentage", example = "30")
        Integer maxFloorDiscount,

        // Long-term parking
        @Min(value = 1, message = "Discount after hours must be at least 1")
        @Schema(description = "Hours after which long-term discount starts", example = "4")
        Integer discountAfterHours,
        @DecimalMin(value = "0.0", message = "Hourly discount rate must be non-negative")
        @DecimalMax(value = "10.0", message = "Hourly discount rate must not exceed 10%")
        @Schema(description = "Discount percentage per additional hour", example = "2.00")
        BigDecimal hourlyDiscountRate,
        @DecimalMin(value = "0.0", message = "Max discount must be non-negative")
        @DecimalMax(value = "90.0", message = "Max discount must not exceed 90%")
        @Schema(description = "Maximum long-term discount percentage", example = "30.00")
        BigDecimal maxDiscountPercentage,
        @Positive(message = "Max daily rate must be positive")
        @Schema(description = "Maximum daily rate cap", example = "15000.00")
        BigDecimal maxDailyRate,

        // Vehicle type multipliers
        @DecimalMin(value = "0.1", message = "Motorcycle discount must be at least 0.1")
        @DecimalMax(value = "1.0", message = "Motorcycle discount must not exceed 1.0")
        @Schema(description = "Motorcycle pricing multiplier (discount)", example = "0.70")
        BigDecimal motorcycleDiscount,
        @DecimalMin(value = "1.0", message = "Truck surcharge must be at least 1.0")
        @DecimalMax(value = "5.0", message = "Truck surcharge must not exceed 5.0")
        @Schema(description = "Truck pricing multiplier (surcharge)", example = "1.50")
        BigDecimal truckSurcharge,

        // Demand-based pricing
        @DecimalMin(value = "0.5", message = "Demand multiplier must be at least 0.5")
        @DecimalMax(value = "5.0", message = "Demand multiplier must not exceed 5.0")
        @Schema(description = "Demand-based pricing multiplier", example = "1.00")
        BigDecimal demandMultiplier,
        @Min(value = 1, message = "Occupancy threshold must be at least 1")
        @Max(value = 100, message = "Occupancy threshold must not exceed 100")
        @Schema(description = "Occupancy percentage threshold for demand pricing", example = "80")
        Integer occupancyThreshold,

        // Weather multipliers
        @DecimalMin(value = "0.5", message = "Weather multiplier must be at least 0.5")
        @DecimalMax(value = "3.0", message = "Weather multiplier must not exceed 3.0")
        @Schema(description = "Rain weather pricing multiplier", example = "1.20")
        BigDecimal weatherMultiplierRain,
        @DecimalMin(value = "0.5", message = "Weather multiplier must be at least 0.5")
        @DecimalMax(value = "3.0", message = "Weather multiplier must not exceed 3.0")
        @Schema(description = "Snow weather pricing multiplier", example = "1.50")
        BigDecimal weatherMultiplierSnow,
        @DecimalMin(value = "0.5", message = "Weather multiplier must be at least 0.5")
        @DecimalMax(value = "3.0", message = "Weather multiplier must not exceed 3.0")
        @Schema(description = "Extreme heat weather pricing multiplier", example = "1.30")
        BigDecimal weatherMultiplierExtremeHeat,

        // Event pricing
        @DecimalMin(value = "0.5", message = "Event multiplier must be at least 0.5")
        @DecimalMax(value = "5.0", message = "Event multiplier must not exceed 5.0")
        @Schema(description = "Event pricing multiplier", example = "1.25")
        BigDecimal eventMultiplier,

        // Validation fields
        @Min(value = 0, message = "Grace period must be non-negative")
        @Max(value = 60, message = "Grace period must not exceed 60 minutes")
        @Schema(description = "Grace period in minutes", example = "15")
        Integer gracePeriodMinutes,
        @Schema(description = "When the rule was created", hidden = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        Instant createdAt,
        @Schema(description = "When the rule was last updated", hidden = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        Instant updatedAt
) {}
