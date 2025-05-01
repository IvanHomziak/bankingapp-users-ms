package com.ihomziak.bankingapp.api.users.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ihomziak.bankingapp.api.users.entity.AppRole;
import com.ihomziak.bankingapp.api.users.entity.Role;
import com.ihomziak.bankingapp.api.users.entity.User;
import com.ihomziak.bankingapp.api.users.repository.RoleRepository;
import com.ihomziak.bankingapp.api.users.repository.UserRepository;
import com.ihomziak.bankingapp.api.users.security.jwt.JwtUtils;
import com.ihomziak.bankingapp.api.users.security.request.LoginRequest;
import com.ihomziak.bankingapp.api.users.security.request.SignupRequest;
import com.ihomziak.bankingapp.api.users.security.response.LoginResponse;
import com.ihomziak.bankingapp.api.users.security.response.MessageResponse;
import com.ihomziak.bankingapp.api.users.security.response.UserInfoResponse;
import com.ihomziak.bankingapp.api.users.security.services.UserDetailsImpl;
import com.ihomziak.bankingapp.api.users.service.UserService;
import com.ihomziak.bankingapp.api.users.service.impl.TotpServiceImpl;
import com.ihomziak.bankingapp.api.users.service.impl.UserServiceImpl;
import com.ihomziak.bankingapp.api.users.util.AuthUtil;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder encoder;
	private final UserService userService;
	private final UserServiceImpl userServiceImpl;
	private final AuthUtil authUtil;
	private final TotpServiceImpl totpServiceImpl;

	@Autowired
	public AuthController(AuthenticationManager authenticationManager,
		JwtUtils jwtUtils,
		UserRepository userRepository,
		RoleRepository roleRepository,
		BCryptPasswordEncoder encoder,
		UserService userService,
		UserServiceImpl userServiceImpl,
		AuthUtil authUtil,
		TotpServiceImpl totpServiceImpl) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.encoder = encoder;
		this.userService = userService;
		this.userServiceImpl = userServiceImpl;
		this.authUtil = authUtil;
		this.totpServiceImpl = totpServiceImpl;
	}

	@PostMapping("/public/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		Authentication authentication;
		try {
			authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		} catch (AuthenticationException exception) {
			Map<String, Object> map = new HashMap<>();
			map.put("message", "Bad credentials");
			map.put("status", false);
			return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
		}

		// Set the authentication
		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

		// Collect roles from the UserDetails
		List<String> roles = userDetails.getAuthorities().stream()
			.map(item -> item.getAuthority())
			.collect(Collectors.toList());

		// Prepare the response body, now including the JWT token directly in the body
		LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

		// Return the response entity with the JWT token included in the response body
		return ResponseEntity.ok(response);
	}

	@PostMapping("/public/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUserName(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(),
			signUpRequest.getEmail(),
			encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Role role;

		if (strRoles == null || strRoles.isEmpty()) {
			role = roleRepository.findByRoleName(AppRole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		} else {
			String roleStr = strRoles.iterator().next();
			if (roleStr.equals("admin")) {
				role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			} else {
				role = roleRepository.findByRoleName(AppRole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			}

			user.setAccountNonLocked(true);
			user.setAccountNonExpired(true);
			user.setCredentialsNonExpired(true);
			user.setEnabled(true);
			user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
			user.setAccountExpiryDate(LocalDate.now().plusYears(1));
			user.setTwoFactorEnabled(false);
			user.setSignUpMethod("email");
		}
		user.setRole(role);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@GetMapping("/user")
	public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
		User user = userService.findByUsername(userDetails.getUsername());

		List<String> roles = userDetails.getAuthorities().stream()
			.map(item -> item.getAuthority())
			.collect(Collectors.toList());

		UserInfoResponse response = new UserInfoResponse(
			user.getUserId(),
			user.getUserName(),
			user.getEmail(),
			user.isAccountNonLocked(),
			user.isAccountNonExpired(),
			user.isCredentialsNonExpired(),
			user.isEnabled(),
			user.getCredentialsExpiryDate(),
			user.getAccountExpiryDate(),
			user.isTwoFactorEnabled(),
			roles
		);

		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/username")
	public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
		return (userDetails != null) ? userDetails.getUsername() : "";
	}

	@PostMapping("/public/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestParam String email) {
		try {
			userServiceImpl.generatePasswordResetToken(email);
			return ResponseEntity.ok(new MessageResponse("Password reset email sent!"));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Error sending password reset email"));
		}
	}

	@PostMapping("/public/reset-password")
	public ResponseEntity<?> forgotPassword(
		@RequestParam String token,
		@RequestParam String newPassword
	) {
		try {
			userServiceImpl.resetPassword(token, newPassword);
			return ResponseEntity.ok(new MessageResponse("Password reset successfully!"));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new MessageResponse("Error resetting password"));
		}
	}

	@PostMapping("/enable-2fa")
	public ResponseEntity<String> enable2FA() {
		Long userId = authUtil.loggedInUserId();
		GoogleAuthenticatorKey secret = userService.generate2faSecretKey(userId);

		String qrCodeUrl = totpServiceImpl.getQrCodeUrl(secret,
			userServiceImpl.getUserById(userId).getUserName()
		);
		return ResponseEntity.ok(qrCodeUrl);
	}

	@PostMapping("/disable-2fa")
	public ResponseEntity<String> disable2FA() {
		Long userId = authUtil.loggedInUserId();

		userServiceImpl.disable2fa(userId);
		return ResponseEntity.ok("2FA disabled successfully");
	}

	@PostMapping("/verify-2fa")
	public ResponseEntity<String> verify2FA(@RequestParam int code) {
		Long userId = authUtil.loggedInUserId();

		boolean isValid = userService.validate2faCode(userId, code);

		if (isValid) {
			userService.enable2fa(userId);
			return ResponseEntity.ok("2FA verified successfully");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code");
		}
	}

	@GetMapping("/user/2fa-status")
	public ResponseEntity<?> get2FAStatus() {
		User user = authUtil.loggedInUser();

		if (user != null) {
			return ResponseEntity.ok().body(Map.of("2faEnabled", user.isTwoFactorEnabled()).toString());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER NOT FOUND");
		}
	}

	@PostMapping("/public/verify-2fa-login")
	public ResponseEntity<String> verify2FALogin(
		@RequestParam int code,
		@RequestParam String jwtToken
	) {

		String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
		User user = userService.findByUsername(username);

		boolean isValid = userService.validate2faCode(user.getUserId(), code);

		if (isValid) {
			userService.enable2fa(user.getUserId());
			return ResponseEntity.ok("2FA verified successfully");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code");
		}
	}
}
