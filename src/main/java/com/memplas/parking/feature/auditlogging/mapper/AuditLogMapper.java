package com.memplas.parking.feature.auditlogging.mapper;

import com.memplas.parking.feature.auditlogging.dto.AuditLogDto;
import com.memplas.parking.feature.auditlogging.model.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface AuditLogMapper {
    @Mapping(target = "userId", source = "user.id")
    AuditLogDto toDto(AuditLog entity);

    @Mapping(target = "user.id", source = "userId")
    AuditLog toEntity(AuditLogDto dto);
}
