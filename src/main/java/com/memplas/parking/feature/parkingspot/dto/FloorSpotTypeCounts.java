package com.memplas.parking.feature.parkingspot.dto;

public record FloorSpotTypeCounts(
        Long facilityId,
        Boolean wheelChairAccessible,
        Long floorId,
        String floorName,
        Integer floorNumber,
        Integer floorRows,
        Integer floorColumns,
        Double floorMaxHeight,
        Long regularSpots,
        Long disabledSpots,
        Long evChargingSpots,
        Long compactSpots,
        Long vipSpots
) {}