package com.memplas.parking.feature.parkingsession.mapper;

import com.memplas.parking.feature.parkingsession.dto.ParkingSessionDto;
import com.memplas.parking.feature.parkingsession.model.ParkingSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface ParkingSessionMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "spotId", source = "spot.id")
    @Mapping(target = "spotFloorName", source = "spot.floor.name")
    @Mapping(target = "spotNumber", source = "spot.spotNumber")
    @Mapping(target = "facilityAddress", source = "spot.facility.address")
    @Mapping(target = "facilityName", source = "spot.facility.name")
    @Mapping(target = "vehicleMake", source = "vehicle.make")
    @Mapping(target = "vehicleModel", source = "vehicle.model")
    @Mapping(target = "vehicleLicensePlate", source = "vehicle.licensePlate")
    ParkingSessionDto toDto(ParkingSession entity);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "spot.id", source = "spotId")
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "violations", ignore = true)
    ParkingSession toEntity(ParkingSessionDto dto);
}
