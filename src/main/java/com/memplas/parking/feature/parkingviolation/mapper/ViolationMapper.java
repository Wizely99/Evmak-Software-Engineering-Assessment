package com.memplas.parking.feature.parkingviolation.mapper;

import com.memplas.parking.feature.parkingviolation.dto.ViolationDto;
import com.memplas.parking.feature.parkingviolation.model.Violation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface ViolationMapper {
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "spotId", source = "spot.id")
    ViolationDto toDto(Violation entity);

    @Mapping(target = "session.id", source = "sessionId")
    @Mapping(target = "vehicle.id", source = "vehicleId")
    @Mapping(target = "spot.id", source = "spotId")
    Violation toEntity(ViolationDto dto);
}
