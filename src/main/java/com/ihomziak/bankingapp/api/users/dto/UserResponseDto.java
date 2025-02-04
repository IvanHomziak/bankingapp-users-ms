package com.ihomziak.bankingapp.api.users.dto;

import java.util.List;

public class UserResponseDto {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
	private List<AlbumResponseDto> albums;

	public List<AlbumResponseDto> getAlbums() {
		return albums;
	}

	public void setAlbums(List<AlbumResponseDto> albums) {
		this.albums = albums;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
