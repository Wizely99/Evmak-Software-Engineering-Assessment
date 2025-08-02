package com.memplas.parking.feature.facility.mapper;

import com.memplas.parking.feature.facility.dto.DailyFacilityStatsDto;
import com.memplas.parking.feature.facility.model.DailyFacilityStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface DailyFacilityStatsMapper {
    @Mapping(target = "facilityId", source = "facility.id")
    @Mapping(target = "facilityName", source = "facility.name")
    DailyFacilityStatsDto toDto(DailyFacilityStats entity);

    @Mapping(target = "facility.id", source = "facilityId")
    DailyFacilityStats toEntity(DailyFacilityStatsDto dto);
}
