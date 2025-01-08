package com.ihomziak.bankingapp.api.users.service.impl;

import com.ihomziak.bankingapp.api.users.dao.UsersRepository;
import com.ihomziak.bankingapp.api.users.dto.UserDto;
import com.ihomziak.bankingapp.api.users.entity.UserEntity;
import com.ihomziak.bankingapp.api.users.service.UsersService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {

        userDetails.setUserId(UUID.randomUUID().toString());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity user = modelMapper.map(userDetails, UserEntity.class);
        user.setEncryptedPassword("test");

        usersRepository.save(user);

        return null;
    }
}
