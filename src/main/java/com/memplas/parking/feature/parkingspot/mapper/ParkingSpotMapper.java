package com.memplas.parking.feature.parkingspot.mapper;

import com.memplas.parking.feature.parkingspot.dto.ParkingSpotDto;
import com.memplas.parking.feature.parkingspot.model.ParkingSpot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface ParkingSpotMapper {
    @Mapping(target = "facilityId", source = "facility.id")
    @Mapping(target = "reservedBy", source = "reservedBy.id")
    @Mapping(target = "floorId", source = "floor.id")
    ParkingSpotDto toDto(ParkingSpot entity);

    @Mapping(target = "facility.id", source = "facilityId")
    @Mapping(target = "reservedBy.id", source = "reservedBy")
    @Mapping(target = "parkingSessions", ignore = true)
    @Mapping(target = "violations", ignore = true)
    @Mapping(source = "floorId", target = "floor.id")
    ParkingSpot toEntity(ParkingSpotDto dto);
}
