package com.memplas.parking.feature.auditlogging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.memplas.parking.feature.auditlogging.model.AuditAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditLogDto(
        @Schema(hidden = true)
        Long id,
        Long userId,
        @NotBlank(message = "Entity type is required")
        @Size(max = 50, message = "Entity type must not exceed 50 characters")
        String entityType,
        @NotNull(message = "Entity ID is required")
        Long entityId,
        @NotNull(message = "Action is required")
        AuditAction action,
        JsonNode oldValues,
        JsonNode newValues,
        @Size(max = 45, message = "IP address must not exceed 45 characters")
        String ipAddress,
        @Size(max = 255, message = "User agent must not exceed 255 characters")
        String userAgent,
        LocalDateTime createdAt
) {}
