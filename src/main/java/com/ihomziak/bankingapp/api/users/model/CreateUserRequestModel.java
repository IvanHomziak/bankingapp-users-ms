package com.ihomziak.bankingapp.api.users.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateUserRequestModel {

    @NotNull(message = "First name cannot be null")
    @Size(min = 2, message = "First must not be less than two characters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Size(min = 2, message = "Last must not be less than two characters")
    private String lastName;

    @NotNull
    @Size(min = 8, max = 16, message = "Password must be equal or grater than 8 characters and less than 16 characters")
    private String password;

    @NotNull(message = "Email cannot be null")
    @Email
    private String email;

    public @NotNull(message = "First name cannot be null") @Size(min = 2, message = "First must not be less than two characters") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull(message = "First name cannot be null") @Size(min = 2, message = "First must not be less than two characters") String firstName) {
        this.firstName = firstName;
    }

    public @NotNull(message = "Last name cannot be null") @Size(min = 2, message = "Last must not be less than two characters") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull(message = "Last name cannot be null") @Size(min = 2, message = "Last must not be less than two characters") String lastName) {
        this.lastName = lastName;
    }

    public @NotNull @Size(min = 8, max = 16, message = "Password must be equal or grater than 8 characters and less than 16 characters") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @Size(min = 8, max = 16, message = "Password must be equal or grater than 8 characters and less than 16 characters") String password) {
        this.password = password;
    }

    public @NotNull(message = "Email cannot be null") @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotNull(message = "Email cannot be null") @Email String email) {
        this.email = email;
    }
}
