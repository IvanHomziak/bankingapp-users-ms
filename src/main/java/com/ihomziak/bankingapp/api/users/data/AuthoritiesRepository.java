package com.ihomziak.bankingapp.api.users.data;

import org.springframework.data.repository.CrudRepository;

public interface AuthoritiesRepository extends CrudRepository<AuthorityEntity, String> {

    AuthorityEntity findByName(String name);
}
