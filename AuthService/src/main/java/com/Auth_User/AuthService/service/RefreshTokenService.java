package com.Auth_User.AuthService.service;


import com.Auth_User.AuthService.model.RefreshToken;
import com.Auth_User.AuthService.model.User;
import com.Auth_User.AuthService.repo.RefreshTokenRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final SecureRandom random = new SecureRandom();

    @Value("${security.jwt.refresh-token-ttl-seconds}")
    private long refreshTokenTtlSec;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    public String createAndStore(User user, String deviceId) {
        String raw = generateRawToken();
        String hash = hash(raw);

        RefreshToken token = RefreshToken.builder()
                .tokenHash(hash)
                .user(user)
                .deviceId(deviceId)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(refreshTokenTtlSec))
                .revoked(false)
                .build();
        repo.save(token);
        return raw;
    }

    public Optional<RefreshToken> verify(String rawToken) {
        String hash = hash(rawToken);
        Optional<RefreshToken> opt = repo.findByTokenHash(hash);
        if (opt.isPresent()) {
            RefreshToken t = opt.get();
            if (t.isRevoked() || t.getExpiresAt().isBefore(Instant.now())) {
                return Optional.empty();
            }
            return Optional.of(t);
        }
        return Optional.empty();
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repo.save(token);
    }

    public void revokeAllForUser(Long userId) {
        repo.deleteAllByUserId(userId);
    }

    private String generateRawToken() {
        byte[] b = new byte[64];
        random.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    private String hash(String raw) {
        return DigestUtils.sha256Hex(raw);
    }
}
