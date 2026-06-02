package com.nutritrack.backend.service;

import com.nutritrack.backend.entity.RefreshToken;
import com.nutritrack.backend.entity.User;
import com.nutritrack.backend.exception.InvalidRefreshTokenException;
import com.nutritrack.backend.repository.RefreshTokenRepository;
import com.nutritrack.backend.security.JwtProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public String createRefreshToken(User user) {
        String rawToken = generateRawToken();
        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiresAt(Instant.now().plusMillis(jwtProperties.refreshExpirationMs()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);
        return rawToken;
    }

    @Transactional
    //ensures token has not expired
    public User validateAndRotate(String rawToken) {
        RefreshToken stored = refreshTokenRepository
                .findByTokenHashAndRevokedFalse(hashToken(rawToken))
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or expired refresh token"));
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return stored.getUser();
    }

    @Transactional
    public void revokeAllForUser(User user) {
        refreshTokenRepository.findByUser_IdAndRevokedFalse(user.getId())
                .forEach(rt -> rt.setRevoked(true));
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
