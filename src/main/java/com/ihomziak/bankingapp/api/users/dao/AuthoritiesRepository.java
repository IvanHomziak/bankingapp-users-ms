package com.ihomziak.bankingapp.api.users.dao;

import com.ihomziak.bankingapp.api.users.entity.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;

public interface AuthoritiesRepository extends CrudRepository<AuthorityEntity, String> {

    AuthorityEntity findByName(String name);
}
