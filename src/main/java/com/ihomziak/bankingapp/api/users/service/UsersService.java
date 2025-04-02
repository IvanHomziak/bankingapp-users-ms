package com.ihomziak.bankingapp.api.users.service;

import com.ihomziak.bankingapp.api.users.dto.CreateUserRequestDto;
import com.ihomziak.bankingapp.api.users.dto.CreateUserResponseDto;
import com.ihomziak.bankingapp.api.users.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {

    CreateUserResponseDto createUser(CreateUserRequestDto userDetails);

    UserDto getUserDetailsByEmail(String email);

    UserDto getUserByUserId(String userId, String authorization);
}