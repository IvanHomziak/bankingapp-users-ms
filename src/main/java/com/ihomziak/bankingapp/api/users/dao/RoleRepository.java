package com.ihomziak.bankingapp.api.users.dao;

import com.ihomziak.bankingapp.api.users.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

	RoleEntity findByName(String name);
	
}
