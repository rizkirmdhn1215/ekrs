package com.bandung.ekrs.service;

import com.bandung.ekrs.dto.auth.LoginRequest;
import com.bandung.ekrs.dto.auth.LoginResponse;
import com.bandung.ekrs.model.Account;
import com.bandung.ekrs.repository.AccountRepository;
import com.bandung.ekrs.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AccountRepository accountRepository;

    public LoginResponse login(LoginRequest request) {
        try {
            log.debug("Attempting authentication for user: {}", request.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            Account account = accountRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));
            
            String token = jwtUtil.generateToken(userDetails);

            return LoginResponse.builder()
                    .id(account.getId())
                    .username(account.getUsername())
                    .email(account.getEmail())
                    .role(account.getRole())
                    .token(token)
                    .build();
                    
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            throw new BadCredentialsException("Invalid username or password");
        }
    }
} 