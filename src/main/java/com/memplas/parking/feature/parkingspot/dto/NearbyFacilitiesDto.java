package com.memplas.parking.feature.parkingspot.dto;

import com.memplas.parking.feature.facility.dto.ParkingFacilityDto;

import java.util.List;

public record NearbyFacilitiesDto(List<ParkingFacilityDto> facilities, List<FacilityAvailabilityDto> availability) {
}
