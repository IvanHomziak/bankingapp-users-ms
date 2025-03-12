package com.ihomziak.bankingapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlbumResponseDto {
    private String albumId;
    private String userId;
    private String name;
    private String description;
}