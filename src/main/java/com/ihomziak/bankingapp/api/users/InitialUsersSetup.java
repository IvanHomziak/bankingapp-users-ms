package com.ihomziak.bankingapp.api.users;

import com.ihomziak.bankingapp.api.users.data.*;
import com.ihomziak.bankingapp.api.users.shared.Roles;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Component
public class InitialUsersSetup {

    private static final Logger logger = LoggerFactory.getLogger(InitialUsersSetup.class);

    private final AuthoritiesRepository authoritiesRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UsersRepository usersRepository;

    @Autowired
    public InitialUsersSetup(AuthoritiesRepository authoritiesRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder, com.ihomziak.bankingapp.api.users.data.UsersRepository usersRepository) {
        this.authoritiesRepository = authoritiesRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this. usersRepository= usersRepository;
    }

    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("Initializing users from Application Ready Event");

        AuthorityEntity readAuthority = createAuthority("READ");
        AuthorityEntity writeAuthority = createAuthority("WRITE");
        AuthorityEntity deleteAuthority = createAuthority("DELETE");

        RoleEntity userRole = createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
        RoleEntity adminRole = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

        if (adminRole == null) return;

        UserEntity user = new UserEntity();
        user.setFirstName("Jack");
        user.setLastName("Sparrow");
        user.setUserId(UUID.randomUUID().toString());
        user.setEmail("jack.sparrow@gmail.com");
        user.setEncryptedPassword(bCryptPasswordEncoder.encode("password"));
        user.setRoles(Arrays.asList(adminRole));

        UserEntity admin = usersRepository.findByEmail(user.getEmail());

        if (admin == null) {
            usersRepository.save(user);
        }

        UserEntity user2 = new UserEntity();
        user2.setFirstName("Jack");
        user2.setLastName("Sparrow");
        user2.setUserId(UUID.randomUUID().toString());
        user2.setEmail("jack.sparrow2@gmail.com");
        user2.setEncryptedPassword(bCryptPasswordEncoder.encode("password"));
        user2.setRoles(Arrays.asList(adminRole));

        UserEntity admin2 = usersRepository.findByEmail(user2.getEmail());

        if (admin2 == null) {
            usersRepository.save(user2);
        }
    }

    @Transactional
    protected AuthorityEntity createAuthority(String name) {
        AuthorityEntity authority = authoritiesRepository.findByName(name);

        if (authority == null) {
            authority = new AuthorityEntity(name);
            authoritiesRepository.save(authority);
        }

        return authority;
    }

    @Transactional
    protected RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        RoleEntity role = roleRepository.findByName(name);

        if (role == null) {
            role = new RoleEntity(name, authorities);
            roleRepository.save(role);
        }

        return role;
    }
}
