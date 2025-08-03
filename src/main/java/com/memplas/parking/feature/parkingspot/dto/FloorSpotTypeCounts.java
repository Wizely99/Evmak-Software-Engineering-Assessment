package com.memplas.parking.feature.parkingspot.dto;

public record FloorSpotTypeCounts(
        Long facilityId,
        Boolean wheelChairAccessible,
        Long floorId,
        String floorName,
        Long regularSpots,
        Long disabledSpots,
        Long evChargingSpots,
        Long compactSpots,
        Long vipSpots
) {}
