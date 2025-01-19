package com.ihomziak.bankingapp.api.users.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import com.ihomziak.bankingapp.api.users.service.UsersService;
import com.ihomziak.bankingapp.api.users.shared.UserDto;
import com.ihomziak.bankingapp.api.users.ui.model.LoginRequestModel;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UsersService usersService;
    private final Environment environment;

    public AuthenticationFilter(UsersService usersService, Environment environment,
                                AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.usersService = usersService;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {

            LoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), LoginRequestModel.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        // Get the username of the authenticated user
        String userName = ((User) auth.getPrincipal()).getUsername();

        // Fetch user details using the username
        UserDto userDetails = usersService.getUserDetailsByEmail(userName);

        // Get token secret and expiration time from environment properties
        String tokenSecret = environment.getProperty("token.secret");
        String expirationTimeStr = environment.getProperty("token.expiration_time");

        if (tokenSecret == null || expirationTimeStr == null) {
            throw new IllegalStateException("Token secret or expiration time not configured properly");
        }

        try {

            byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
            SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

            Instant now = Instant.now();

            // Generate JWT token
            String token = Jwts.builder()
                    .claim("scope", auth.getAuthorities())
                    .subject(userDetails.getUserId())
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plusMillis(Long.parseLong(expirationTimeStr))))
                    .signWith(secretKey)
                    .compact();

            // Add token and userId to the response headers
            res.addHeader("token", token);
            res.addHeader("userId", userDetails.getUserId());

            logger.info("Successful authentication for user: {} " + userName);
        } catch (Exception e) {
            logger.error("Error during successful authentication: {}", e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("An error occurred during authentication");
        }
    }
}
