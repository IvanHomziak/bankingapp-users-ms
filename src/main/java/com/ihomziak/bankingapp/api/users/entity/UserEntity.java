package com.ihomziak.bankingapp.api.users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "email", length = 120, unique = true)
    private String email;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "encrypted_password", unique = true)
    private String encryptedPassword;
}
