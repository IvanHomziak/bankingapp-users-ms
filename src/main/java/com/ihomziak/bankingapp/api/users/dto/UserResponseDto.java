package com.ihomziak.bankingapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
}
