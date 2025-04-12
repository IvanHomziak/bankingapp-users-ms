package com.ihomziak.bankingapp.api.users.shared.constants;

public class Endpoints {

	public static final String API_PREFIX = "/api/v1/users";

	public static final String CREATE_USER = API_PREFIX + "/create";
	public static final String LOGIN_USER = API_PREFIX + "/login";
	public static final String REGEX = "/**";
	public static final String GET_USER = API_PREFIX + REGEX;
	public static final String DELETE_USER = API_PREFIX + REGEX;
	public static final String STATUS_CHECK = API_PREFIX + "/status/check";
}