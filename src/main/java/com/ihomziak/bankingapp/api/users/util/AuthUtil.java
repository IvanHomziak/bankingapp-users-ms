package com.ihomziak.bankingapp.api.users.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ihomziak.bankingapp.api.users.entity.User;
import com.ihomziak.bankingapp.api.users.repository.UserRepository;

@Component
public class AuthUtil {

	@Autowired
	UserRepository userRepository;

	public Long loggedInUserId() {
		Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepository.findByUserName(authentication.getName())
			.orElseThrow(() -> new RuntimeException("User not found"));

		return user.getUserId();
	}

	public User loggedInUser() {
		Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();

		return userRepository.findByUserName(authentication.getName())
			.orElseThrow(() -> new RuntimeException("User not found"));
	}
}
