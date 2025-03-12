package com.ihomziak.bankingapp.api.users.dao;

import com.ihomziak.bankingapp.api.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);
}