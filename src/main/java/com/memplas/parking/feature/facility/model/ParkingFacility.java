package com.memplas.parking.feature.facility.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.memplas.parking.core.model.BaseEntity;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
import com.memplas.parking.feature.pricing.model.PricingRule;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Type;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parking_facilities")
public class ParkingFacility extends BaseEntity {
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private Integer totalFloorCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Facility type is required")
    private FacilityType facilityType;

    @Column(nullable = false)
    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Column(nullable = false, precision = 10)
    private Double locationLat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Column(nullable = false, precision = 11)
    private Double locationLng;

    @Column(nullable = false)
    @NotNull(message = "Total spots is required")
    private Integer totalSpots;

    private LocalTime operatingHoursStart;

    private LocalTime operatingHoursEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityStatus status = FacilityStatus.ACTIVE;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private JsonNode features;

    private Integer maxHeightCm;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ParkingSpot> parkingSpots = new HashSet<>();

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PricingRule> pricingRules = new HashSet<>();

    // Constructors
    public ParkingFacility() {}

    public ParkingFacility(Long id) {
        super(id);
    }

    public Integer getTotalFloorCount() {
        return totalFloorCount;
    }

    public void setTotalFloorCount(Integer totalFloorCount) {
        this.totalFloorCount = totalFloorCount;
    }

    // Getters and Setters
    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public FacilityType getFacilityType() {return facilityType;}

    public void setFacilityType(FacilityType facilityType) {this.facilityType = facilityType;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public Double getLocationLat() {return locationLat;}

    public void setLocationLat(Double locationLat) {this.locationLat = locationLat;}

    public Double getLocationLng() {return locationLng;}

    public void setLocationLng(Double locationLng) {this.locationLng = locationLng;}

    public Integer getTotalSpots() {return totalSpots;}

    public void setTotalSpots(Integer totalSpots) {this.totalSpots = totalSpots;}

    public LocalTime getOperatingHoursStart() {return operatingHoursStart;}

    public void setOperatingHoursStart(LocalTime operatingHoursStart) {this.operatingHoursStart = operatingHoursStart;}

    public LocalTime getOperatingHoursEnd() {return operatingHoursEnd;}

    public void setOperatingHoursEnd(LocalTime operatingHoursEnd) {this.operatingHoursEnd = operatingHoursEnd;}

    public FacilityStatus getStatus() {return status;}

    public void setStatus(FacilityStatus status) {this.status = status;}

    public JsonNode getFeatures() {return features;}

    public void setFeatures(JsonNode features) {this.features = features;}

    public Integer getMaxHeightCm() {return maxHeightCm;}

    public void setMaxHeightCm(Integer maxHeightCm) {this.maxHeightCm = maxHeightCm;}

    public Set<ParkingSpot> getParkingSpots() {return parkingSpots;}

    public void setParkingSpots(Set<ParkingSpot> parkingSpots) {this.parkingSpots = parkingSpots;}

    public Set<PricingRule> getPricingRules() {return pricingRules;}

    public void setPricingRules(Set<PricingRule> pricingRules) {this.pricingRules = pricingRules;}

}
