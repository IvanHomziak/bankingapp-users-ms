package com.ihomziak.bankingapp.api.users.controller;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminController - Access Control Tests")
class AdminControllerTest {

    @BeforeAll
    static void setup() {

    }

    @AfterAll
    static void tearDown() {

    }

    @BeforeEach
    void beforeEachTestMethod() {

    }

    @AfterEach
    void afterEachTestMethod() {

    }

    @Disabled
    @Test
    @DisplayName("Should return all users when accessed by an admin user")
    void shouldReturnAllUsers_WhenAccessedByAdmin() {
        // TODO: Arrange (mock admin authentication and user service) - Given

        // TODO: Act (call controller method) - When

        // TODO: Assert (verify all users are returned) - Then
    }

    @Test
    @DisplayName("Should throw ForbiddenException when accessed by a non-admin user")
    void shouldThrowForbiddenException_WhenAccessedByNonAdmin() {
        // TODO: Arrange (mock non-admin authentication)

        // TODO: Act & Assert (expect exception to be thrown)
    }

    @Test
    @DisplayName("Should throw ForbiddenException when accessed by a non-registered user")
    void shouldThrowForbiddenException_WhenAccessedByNonRegisteredUser() {
        // TODO: Arrange (mock non-admin authentication)

        // TODO: Act & Assert (expect exception to be thrown)
    }

    @Test
    void updateUserRole() {
    }

    @Test
    void getUser() {
    }

    @Test
    void updateAccountLockStatus() {
    }

    @Test
    void getAllRoles() {
    }

    @Test
    void updateAccountExpiryStatus() {
    }

    @Test
    void updateAccountEnabledStatus() {
    }

    @Test
    void updateCredentialsExpiryStatus() {
    }

    @Test
    void updatePassword() {
    }
}