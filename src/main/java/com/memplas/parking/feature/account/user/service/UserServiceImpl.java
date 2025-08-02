package com.memplas.parking.feature.account.user.service;

import com.memplas.parking.core.exception.UserCreationException;
import com.memplas.parking.feature.account.user.dto.UserDto;
import com.memplas.parking.feature.account.user.helper.AuthenticatedUserProvider;
import com.memplas.parking.feature.account.user.mapper.UserMapper;
import com.memplas.parking.feature.account.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userProfileRepository;

    private final KeycloakAdminService keycloakAdminService;

    private final UserMapper userProfileMapper;

    public UserServiceImpl(
            UserRepository userProfileRepository,
            KeycloakAdminService keycloakAdminService,
            AuthenticatedUserProvider authenticatedUserProvider,
            UserMapper userProfileMapper) {
        this.userProfileRepository = userProfileRepository;
        this.keycloakAdminService = keycloakAdminService;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public UserDto createUser(
            UserDto userDto) {

        String keycloakUserId = keycloakAdminService.createUser(userProfileMapper.toKeycloakUserDto(userDto));

        UUID userId;
        try {
            userId = UUID.fromString(keycloakUserId);
        } catch (Exception e) {
            throw new UserCreationException(MessageFormat.format("Failed to create user profile for {0}", userDto.email()));
        }
        var user = userProfileMapper.toEntity(userDto);
        user.setKeycloakUserId(userId.toString());

        var savedUser = userProfileRepository.save(user);
        return userProfileMapper.toDto(savedUser);

    }

    @Override
    public UserDto getUser(Long id) {
        return userProfileRepository
                .findById(id)
                .map(userProfileMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));
    }

    @Override
    @Deprecated(forRemoval = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userProfileRepository.findAll(pageable).map(userProfileMapper::toDto);
    }


}
