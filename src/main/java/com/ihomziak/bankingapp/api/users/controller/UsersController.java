package com.ihomziak.bankingapp.api.users.controller;

import static com.ihomziak.bankingapp.api.users.shared.constants.Endpoints.API_PREFIX;

import com.ihomziak.bankingapp.api.users.service.UsersService;
import com.ihomziak.bankingapp.api.users.shared.UserDto;
import com.ihomziak.bankingapp.api.users.dto.CreateUserRequestDto;
import com.ihomziak.bankingapp.api.users.dto.CreateUserResponseDto;
import com.ihomziak.bankingapp.api.users.dto.UserResponseDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_PREFIX)
public class UsersController {

    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private Environment env;

    @Autowired
    UsersService usersService;

    @GetMapping("/status/check")
    public String status() {
        return "Working on port " + env.getProperty("local.server.port") + ", with token = " + env.getProperty("token.secret");
    }

    @PostMapping(
        path = "/create",
        consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<CreateUserResponseDto> createUser(@RequestBody CreateUserRequestDto userDetails) {
        log.info("Create user : {}", userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.createUser(userDetails));
    }

    @GetMapping(
            value = "/{userId}",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_JSON_VALUE
            })
    @PreAuthorize("hasRole('ADMIN') or principal == #userId")
    //@PreAuthorize("principal == #userId")
    //@PostAuthorize("principal == returnObject.body.userId")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable("userId") String userId,
                                                   @RequestHeader("Authorization") String authorization) {

        UserDto userDto = usersService.getUserByUserId(userId, authorization);
        UserResponseDto returnValue = new ModelMapper().map(userDto, UserResponseDto.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_DELETE') or principal == #userId")
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable("userId") String userId) {

        // Delete user logic here

        return "Deleting user with id " + userId;
    }
}
