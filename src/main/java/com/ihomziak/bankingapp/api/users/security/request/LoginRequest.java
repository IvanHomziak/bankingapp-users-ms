package com.ihomziak.bankingapp.api.users.security.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
	private String username;

	private String password;

}
