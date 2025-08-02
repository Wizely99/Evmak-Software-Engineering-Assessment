package com.memplas.parking.feature.account.user.mapper;

import com.memplas.parking.feature.account.user.dto.KeycloakUserDto;
import com.memplas.parking.feature.account.user.dto.UserDto;
import com.memplas.parking.feature.book.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface UserMapper {
    UserDto toDto(User entity);

    User toEntity(UserDto dto);

    KeycloakUserDto toKeycloakUserDto(UserDto dto);

    User fromKeycloakUserDto(KeycloakUserDto dto);
}

