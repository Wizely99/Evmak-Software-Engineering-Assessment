package com.memplas.parking.feature.pricing.mapper;

import com.memplas.parking.feature.pricing.dto.PricingRuleDto;
import com.memplas.parking.feature.pricing.model.PricingRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface PricingRuleMapper {
    @Mapping(target = "facilityId", source = "facility.id")
    PricingRuleDto toDto(PricingRule entity);

    @Mapping(target = "facility.id", source = "facilityId")
    PricingRule toEntity(PricingRuleDto dto);
}
