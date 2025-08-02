package com.memplas.parking.feature.account.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record KeycloakUserDto(
        @Schema(hidden = true)
        UUID keycloakId,
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber,
        boolean isAdmin
) {
}
