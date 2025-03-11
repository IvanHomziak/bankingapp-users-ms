package com.ihomziak.bankingapp.api.users.dto;

import lombok.*;

@Data
@Getter
@Setter
public class LoginRequestDto {
	private String email;
	private String password;
}
