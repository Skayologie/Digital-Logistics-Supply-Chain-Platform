package com.project.supplychain.JWT;

import com.project.supplychain.models.RefreshToken;
import com.project.supplychain.models.user.Admin;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.User;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.repositories.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static java.util.Objects.hash;

@Component
public class JWT {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String generateToken(User user) {
        if(user instanceof WarehouseManager){
            return Jwts.builder()
                    .subject(user.getEmail())
                    .claim("role", ((WarehouseManager) user).getRole().name())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey())
                    .compact();
        }else if (user instanceof Client){
            return Jwts.builder()
                    .subject(user.getEmail())
                    .claim("role", ((Client) user).getRole().name())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey())
                    .compact();
        }else if (user instanceof Admin){
            return Jwts.builder()
                    .subject(user.getEmail())
                    .claim("role", ((Admin) user).getRole().name())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey())
                    .compact();
        } else {
            return Jwts.builder()
                    .subject(user.getEmail())
                    .claim("role", "")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey())
                    .compact();
        }
    }
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String generateRefreshTokenRaw() {
        return UUID.randomUUID() + "." + UUID.randomUUID();
    }


    public void saveRefreshToken(User user, String rawRefreshToken) {

        String hashedToken = hashToken(rawRefreshToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashedToken);

        refreshToken.setExpiresAt(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        refreshTokenRepository.save(refreshToken);
    }
    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
    public RefreshToken verifyRefreshToken(String rawRefreshToken) {
        String hashedToken = hashToken(rawRefreshToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new RuntimeException("Refresh Token not found"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token); // Clean up expired token
            throw new RuntimeException("Refresh Token expired. Please login again.");
        }

        return token;
    }

}
