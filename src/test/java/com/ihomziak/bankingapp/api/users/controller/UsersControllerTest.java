package com.ihomziak.bankingapp.api.users.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ihomziak.bankingapp.api.users.dto.CreateUserRequestDto;
import com.ihomziak.bankingapp.api.users.dto.CreateUserResponseDto;
import com.ihomziak.bankingapp.api.users.dto.UserResponseDto;
import com.ihomziak.bankingapp.api.users.service.UsersService;
import com.ihomziak.bankingapp.api.users.shared.UserDto;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @Mock
    private Environment env;

    @InjectMocks
    private UsersController usersController;

    private ModelMapper modelMapper;
    private UserDto mockUserDto;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        mockUserDto = new UserDto();
        mockUserDto.setUserId("12345");
        mockUserDto.setEmail("test@example.com");
        mockUserDto.setFirstName("John");
        mockUserDto.setLastName("Doe");
    }

    @Test
    void testStatus() {
        when(env.getProperty("local.server.port")).thenReturn("8080");
        when(env.getProperty("token.secret")).thenReturn("testSecret");

        String status = usersController.status();

        assertEquals("Working on port 8080, with token = testSecret", status);
    }

//    @Test
//    void testCreateUser() {
//        CreateUserRequestDto requestDto = CreateUserRequestDto.builder()
//            .email("test@example.com")
//            .firstName("John")
//            .lastName("Doe")
//            .build();
//
//        when(usersService.createUser(any(UserDto.class))).thenReturn(mockUserDto);
//
//        ResponseEntity<CreateUserResponseDto> response = usersController.createUser(requestDto);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("John", Objects.requireNonNull(response.getBody()).getFirstName());
//        assertEquals("Doe", response.getBody().getLastName());
//    }

//    @Test
//    void testGetUser() {
//        when(usersService.getUserByUserId(eq("12345"), any())).thenReturn(mockUserDto);
//
//        ResponseEntity<UserResponseDto> response = usersController.getUser("12345", "Bearer token");
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("John", Objects.requireNonNull(response.getBody()).getFirstName());
//        assertEquals("Doe", response.getBody().getLastName());
//    }
//
//    @Test
//    void testDeleteUser() {
//        String response = usersController.deleteUser("12345");
//
//        assertEquals("Deleting user with id 12345", response);
//    }
}