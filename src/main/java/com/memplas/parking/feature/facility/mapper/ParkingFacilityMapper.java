package com.memplas.parking.feature.facility.mapper;

import com.memplas.parking.feature.facility.dto.ParkingFacilityDto;
import com.memplas.parking.feature.facility.model.ParkingFacility;
import org.mapstruct.*;

import java.time.LocalTime;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface ParkingFacilityMapper {
    ParkingFacilityDto toDto(ParkingFacility entity);

    @Mapping(target = "parkingSpots", ignore = true)
    @Mapping(target = "pricingRules", ignore = true)
    @Mapping(target = "operatingHoursStart", source = "operatingHoursStart", qualifiedByName = "stringToLocalTime")
    @Mapping(target = "operatingHoursEnd", source = "operatingHoursEnd", qualifiedByName = "stringToLocalTime")
    ParkingFacility toEntity(ParkingFacilityDto dto);

    @Named("stringToLocalTime")
    public static LocalTime stringToLocalTime(String time) {
        return time != null ? LocalTime.parse(time) : null;
    }

}
