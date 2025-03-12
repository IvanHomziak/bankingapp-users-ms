package com.ihomziak.bankingapp.api.users.dao;

import com.ihomziak.bankingapp.api.users.entity.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<AuthorityEntity, Long> {

    AuthorityEntity findByName(String name);

}