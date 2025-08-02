package com.memplas.parking.feature.facility.mapper;

import com.memplas.parking.feature.facility.dto.FacilityAvailabilityDto;
import com.memplas.parking.feature.facility.model.FacilityAvailabilityCache;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface FacilityAvailabilityMapper {
    @Mapping(target = "facilityId", source = "facility.id")
    @Mapping(target = "facilityName", source = "facility.name")
    @Mapping(target = "address", source = "facility.address")
    @Mapping(target = "locationLat", source = "facility.locationLat")
    @Mapping(target = "locationLng", source = "facility.locationLng")
    @Mapping(target = "currentHourlyRate", ignore = true)
        // calculated separately
    FacilityAvailabilityDto toDto(FacilityAvailabilityCache cache);

    // Custom mapping for search results
    @Mapping(target = "facilityId", source = "id")
    @Mapping(target = "facilityName", source = "name")
    @Mapping(target = "totalSpots", ignore = true) // from cache
    @Mapping(target = "availableSpots", ignore = true) // from cache
    @Mapping(target = "occupiedSpots", ignore = true) // from cache
    @Mapping(target = "reservedSpots", ignore = true) // from cache
    @Mapping(target = "outOfOrderSpots", ignore = true) // from cache
    @Mapping(target = "currentOccupancyRate", ignore = true) // from cache
    @Mapping(target = "currentHourlyRate", ignore = true) // calculated
    @Mapping(target = "lastUpdated", ignore = true)
    // from cache
    FacilityAvailabilityDto facilityToAvailabilityDto(ParkingFacility facility);
}
