package com.ihomziak.bankingapp.api.users.security;

import java.io.IOException;
import java.util.Objects;

import com.ihomziak.bankingapp.common.api.JwtClaimsParser;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final Environment environment;

    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               Environment environment) {
        super(authenticationManager);
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        if (!req.getMethod().startsWith("/users") & !req.getMethod().equals("POST")) {
            String authorizationHeader = req.getHeader(environment.getProperty("authorization.token.header.name"));
            if (authorizationHeader == null
                    || !authorizationHeader.startsWith(Objects.requireNonNull(environment.getProperty("authorization.token.header.prefix")))) {
                chain.doFilter(req, res);
                return;
            }
        }


        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
        String authorizationHeader = req.getHeader(environment.getProperty("authorization.token.header.name"));

        if (authorizationHeader == null) {
            return null;
        }

        String token = authorizationHeader.replace(environment.getProperty("authorization.token.header.prefix"), "").trim();
        String tokenSecret = environment.getProperty("token.secret");

        if (tokenSecret == null) return null;

        JwtClaimsParser jwtClaimsParser = new JwtClaimsParser(token, tokenSecret);
        String userId = jwtClaimsParser.getJwtSubject();

        if (userId == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(userId, null, jwtClaimsParser.getUserAuthorities());

    }
}