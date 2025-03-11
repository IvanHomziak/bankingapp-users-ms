package com.ihomziak.bankingapp.api.users.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
	private List<AlbumResponseDto> albums;
}
