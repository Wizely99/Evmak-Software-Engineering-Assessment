package com.memplas.parking.feature.account.user.mapper;

import com.memplas.parking.feature.account.user.dto.KeycloakUserDto;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface KeycloakUserMapper {
    //
    UserRepresentation toEntity(KeycloakUserDto dto, boolean isAdmin);

    @AfterMapping
    default void setCustomAttributes(KeycloakUserDto keycloakUserDto, @MappingTarget UserRepresentation user,
                                     boolean isAdmin) {
        // Set custom attributes
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("phoneNumber", List.of(String.valueOf(keycloakUserDto.phoneNumber())));
        attributes.put("isAdmin", List.of(String.valueOf(isAdmin)));
        user.setAttributes(attributes);

        // Set required actions
        user.setRequiredActions(List.of("UPDATE_PASSWORD"));

        // Set credentials
        user.setCredentials(Collections.singletonList(createCredentialRepresentation(keycloakUserDto.password())));
    }

    default CredentialRepresentation createCredentialRepresentation(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }
}
