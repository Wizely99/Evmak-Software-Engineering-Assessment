package com.memplas.parking.feature.facility.mapper;

import com.memplas.parking.feature.facility.dto.ParkingFacilityDto;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface ParkingFacilityMapper {
    ParkingFacilityDto toDto(ParkingFacility entity);

    @Mapping(target = "parkingSpots", ignore = true)
    @Mapping(target = "pricingRules", ignore = true)
    @Mapping(target = "availabilityCache", ignore = true)
    ParkingFacility toEntity(ParkingFacilityDto dto);
}
