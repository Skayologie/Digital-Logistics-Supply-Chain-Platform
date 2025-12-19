package com.project.supplychain.services;


import com.project.supplychain.models.RefreshToken;
import com.project.supplychain.models.user.User;
import com.project.supplychain.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshService {
    private final RefreshTokenRepository refreshTokenRepository;

    public String generateRefreshTokenRaw() {
        return UUID.randomUUID().toString();
    }

    public String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    /**
     * Hashes the raw token and saves it to the database linked to the user.
     */
    public void saveRefreshToken(User user, String rawToken, Instant expiresAt) {
        RefreshToken token = new RefreshToken();

        token.setUser(user);
        token.setTokenHash(hash(rawToken)); // 🔒 IMPORTANT: Hash before saving
        token.setExpiresAt(expiresAt);
        token.setRevoked(false); // Active by default

        refreshTokenRepository.save(token);
    }
}
