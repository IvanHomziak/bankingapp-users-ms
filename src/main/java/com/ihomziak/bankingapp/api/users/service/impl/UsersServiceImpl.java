package com.ihomziak.bankingapp.api.users.service.impl;

import com.ihomziak.bankingapp.api.users.dao.AlbumsServiceClient;
import com.ihomziak.bankingapp.api.users.dao.UsersRepository;
import com.ihomziak.bankingapp.api.users.entity.AuthorityEntity;
import com.ihomziak.bankingapp.api.users.entity.RoleEntity;
import com.ihomziak.bankingapp.api.users.entity.UserEntity;
import com.ihomziak.bankingapp.api.users.service.UsersService;
import com.ihomziak.bankingapp.api.users.shared.UserDto;
import com.ihomziak.bankingapp.api.users.dto.AlbumResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UsersServiceImpl implements UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment environment;
    private final AlbumsServiceClient albumsServiceClient;


    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder, RestTemplate restTemplate,
                            Environment environment, AlbumsServiceClient albumsServiceClient) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.environment = environment;
        this.albumsServiceClient = albumsServiceClient;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {
        // TODO Auto-generated method stub

        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

        usersRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(username);

        if (userEntity == null) throw new UsernameNotFoundException(username);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Collection<RoleEntity> roles = userEntity.getRoles();

        roles.forEach((role) -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));

            Collection<AuthorityEntity> authorityEntities = role.getAuthorities();
            authorityEntities.forEach((authorityEntity) -> {
                authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
            });
        });

        return new User(userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                true, true, true, true,
                authorities);
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = usersRepository.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);


        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId, String authorization) {

        UserEntity userEntity = usersRepository.findByUserId(userId);
        if (userEntity == null) throw new UsernameNotFoundException("User not found");

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        List<AlbumResponseDto> albumsList = albumsServiceClient.getAlbums(userId);

        logger.info("Before calling albums Microservice");
        userDto.setAlbums(albumsList);
        logger.info("After calling albums Microservice");

        return userDto;
    }
}