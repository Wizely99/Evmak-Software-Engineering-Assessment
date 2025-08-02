package com.memplas.parking.feature.vehicle.mapper;

import com.memplas.parking.feature.vehicle.dto.VehicleDto;
import com.memplas.parking.feature.vehicle.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface VehicleMapper {
    @Mapping(target = "userId", source = "user.id")
    VehicleDto toDto(Vehicle entity);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "parkingSessions", ignore = true)
    @Mapping(target = "violations", ignore = true)
    Vehicle toEntity(VehicleDto dto);
}
