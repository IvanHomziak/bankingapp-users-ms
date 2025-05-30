package com.ihomziak.bankingapp.api.users.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ihomziak.bankingapp.api.users.dto.UserDTO;
import com.ihomziak.bankingapp.api.users.entity.AppRole;
import com.ihomziak.bankingapp.api.users.entity.PasswordResetToken;
import com.ihomziak.bankingapp.api.users.entity.Role;
import com.ihomziak.bankingapp.api.users.entity.User;
import com.ihomziak.bankingapp.api.users.repository.PasswordResetTokenRepository;
import com.ihomziak.bankingapp.api.users.repository.RoleRepository;
import com.ihomziak.bankingapp.api.users.repository.UserRepository;
import com.ihomziak.bankingapp.api.users.service.UserService;
import com.ihomziak.bankingapp.api.users.util.EmailService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Service
public class UserServiceImpl implements UserService {

	@Value("${frontend.url}")
	private String frontendUrl;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	TotpServiceImpl totpService;

	@Override
	public void updateUserRole(Long userId, String roleName) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		AppRole appRole = AppRole.valueOf(roleName);
		Role role = roleRepository.findByRoleName(appRole)
			.orElseThrow(() -> new RuntimeException("Role not found"));
		user.setRole(role);
		userRepository.save(user);
	}


	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}


//	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Override
	public UserDTO getUserById(Long id) {
		//        return userRepository.findById(id).orElseThrow();
		User user = userRepository.findById(id).orElseThrow();
		return convertToDto(user);
	}

	private UserDTO convertToDto(User user) {
		return new UserDTO(
			user.getUserId(),
			user.getUserName(),
			user.getEmail(),
			user.isAccountNonLocked(),
			user.isAccountNonExpired(),
			user.isCredentialsNonExpired(),
			user.isEnabled(),
			user.getCredentialsExpiryDate(),
			user.getAccountExpiryDate(),
			user.getTwoFactorSecret(),
			user.isTwoFactorEnabled(),
			user.getSignUpMethod(),
			user.getRole(),
			user.getCreatedDate(),
			user.getUpdatedDate()
		);
	}

	@Override
	public User findByUsername(String username) {
		Optional<User> user = userRepository.findByUserName(username);
		return user.orElseThrow(() -> new RuntimeException("User not found with username: " + username));
	}

	@Override
	public void updatePassword(Long userId, String password) {
		try {
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
			user.setPassword(passwordEncoder.encode(password));
			userRepository.save(user);
		} catch (Exception e) {
			throw new RuntimeException("Failed to update password");
		}
	}

	@Override
	public void updateAccountLockStatus(Long userId, boolean lock) {
		User user = userRepository.findById(userId).orElseThrow(()
			-> new RuntimeException("User not found"));
		user.setAccountNonLocked(!lock);
		userRepository.save(user);
	}

	@Override
	public void updateAccountExpiryStatus(Long userId, boolean expire) {
		User user = userRepository.findById(userId).orElseThrow(()
			-> new RuntimeException("User not found"));
		user.setAccountNonExpired(!expire);
		userRepository.save(user);
	}

	@Override
	public void updateAccountEnabledStatus(Long userId, boolean enabled) {
		User user = userRepository.findById(userId).orElseThrow(()
			-> new RuntimeException("User not found"));
		user.setEnabled(enabled);
		userRepository.save(user);
	}

	@Override
	public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
		User user = userRepository.findById(userId).orElseThrow(()
			-> new RuntimeException("User not found"));
		user.setCredentialsNonExpired(!expire);
		userRepository.save(user);
	}

	@Override
	public void generatePasswordResetToken(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
		String token = UUID.randomUUID().toString();
		Instant expirationDate = Instant.now().plus(24, ChronoUnit.HOURS);

		PasswordResetToken passwordResetToken = new PasswordResetToken(token, expirationDate, user);
		passwordResetTokenRepository.save(passwordResetToken);

		String resetUrl = frontendUrl + "/reset-password?token=" + token;
		// Send email with resetUrl
		emailService.sendPasswordResetMail(
			user.getEmail(),
			resetUrl
		);
	}

	@Override
	public void resetPassword(final String token, final String newPassword) {
		PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
			.orElseThrow(() -> new RuntimeException("Invalid token"));

		if (passwordResetToken.isUsed()) {
			throw new RuntimeException("Token already used");
		}

		if (passwordResetToken.getExpiryDate().isBefore(Instant.now())) {
			throw new RuntimeException("Password reset token expired");
		}

		User user = passwordResetToken.getUser();
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		passwordResetToken.setUsed(true);
		passwordResetTokenRepository.save(passwordResetToken);
	}

	@Override
	public Optional<User> findByEmail(final String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User registerUser(final User newUser) {
		if (newUser.getPassword() != null) {
			newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		}

		return userRepository.save(newUser);
	}

	@Override
	public GoogleAuthenticatorKey generate2faSecretKey(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		GoogleAuthenticatorKey key = totpService.generateSecret();
		user.setTwoFactorSecret(key.getKey());
		userRepository.save(user);
		return key;
	}

	@Override
	public boolean validate2faCode(Long userId, int code) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		if (user.getTwoFactorSecret() == null) {
			throw new RuntimeException("2FA is not enabled for this user");
		}

		return totpService.verifyCode(user.getTwoFactorSecret(), code);
	}

	@Override
	public void enable2fa(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));
		user.setTwoFactorEnabled(true);
		userRepository.save(user);
	}

	@Override
	public void disable2fa(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));
		user.setTwoFactorEnabled(false);
		userRepository.save(user);
	}
}
