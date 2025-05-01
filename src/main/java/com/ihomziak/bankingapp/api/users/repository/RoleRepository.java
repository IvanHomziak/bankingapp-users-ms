package com.ihomziak.bankingapp.api.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ihomziak.bankingapp.api.users.entity.AppRole;
import com.ihomziak.bankingapp.api.users.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByRoleName(AppRole appRole);
}
