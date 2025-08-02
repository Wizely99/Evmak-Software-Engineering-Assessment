package com.memplas.parking.feature.account.user.service;

import com.memplas.parking.feature.account.user.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(Long id);

    Page<UserDto> getAllUsers(Pageable pageable);

}
