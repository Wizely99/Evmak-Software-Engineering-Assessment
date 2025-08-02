package com.memplas.parking.feature.parkingspot.dto;
// Or if you prefer a simpler approach:

// Enhanced DTO with facility ID
public record FacilitySpotCounts(
        Long facilityId,
        Long availableSpots,
        Long occupiedSpots,
        Long reservedSpots,
        Long outOfOrderSpots
) {
    public long getTotalSpots() {
        return availableSpots + occupiedSpots + reservedSpots + outOfOrderSpots;
    }
}