package com.project.supplychain.models;

import com.project.supplychain.models.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
public class RefreshToken {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    private Instant createdAt = Instant.now();
}
