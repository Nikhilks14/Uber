package com.Auth_User.AuthService.service;

import com.Auth_User.AuthService.dto.AuthResponse;
import com.Auth_User.AuthService.dto.LoginRequest;
import com.Auth_User.AuthService.dto.SignupRequest;
import com.Auth_User.AuthService.dto.SignupResponse;
import com.Auth_User.AuthService.model.RefreshToken;
import com.Auth_User.AuthService.model.Role;
import com.Auth_User.AuthService.model.User;
import com.Auth_User.AuthService.repo.RoleRepository;
import com.Auth_User.AuthService.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }


    public SignupResponse signup(SignupRequest req) {
        Role role = roleRepo.findByName(req.getRole())
                .orElseGet(() -> roleRepo.save(Role.builder().name(req.getRole()).build()));

        User user = User.builder()
                .email(req.getEmail())
                .phone(req.getPhone())
                .fullName(req.getFullName())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .emailVerified(false)
                .enabled(true)
                .createdAt(Instant.now())
                .build();
        user = userRepo.save(user);
        return SignupResponse.of(user.getId(), "CREATED");
    }

    public AuthResponse login(LoginRequest req, String deviceId) {
        Optional<User> userOpt = userRepo.findByEmail(req.getUsername());
        if (userOpt.isEmpty()) {
            userOpt = userRepo.findByPhone(req.getUsername());
            if (userOpt.isEmpty()) throw new RuntimeException("Invalid credentials");
        }
        User user = userOpt.get();
        if (user.getPasswordHash() == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid credentials");

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole().getName());
        String refreshToken = refreshTokenService.createAndStore(user, deviceId);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresInSeconds(Long.parseLong(System.getProperty("security.jwt.access-token-ttl-seconds", "900")))
                .build();
    }

    public AuthResponse refresh(String rawRefreshToken, String deviceId) {
        RefreshToken token = refreshTokenService.verify(rawRefreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        User user = token.getUser();

        // rotate: revoke old token, issue new
        refreshTokenService.revoke(token);
        String newRefresh = refreshTokenService.createAndStore(user, deviceId);
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole().getName());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefresh)
                .expiresInSeconds(Long.parseLong(System.getProperty("security.jwt.access-token-ttl-seconds", "900")))
                .build();
    }

    public void logout(String rawRefreshToken) {
        refreshTokenService.verify(rawRefreshToken).ifPresent(refreshTokenService::revoke);
    }
}
