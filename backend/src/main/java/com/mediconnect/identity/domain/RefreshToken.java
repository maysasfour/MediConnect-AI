package com.mediconnect.identity.domain;

import com.mediconnect.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * A refresh token in a rotating token family. Only the SHA-256 hash of the token
 * is persisted, never the raw value. Rotation replaces a token on each use;
 * detecting reuse of an already-rotated token revokes the whole family, which
 * defeats token theft.
 */
@Entity
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "rotated_to")
    private UUID rotatedTo;

    protected RefreshToken() {
    }

    public RefreshToken(UUID userId, String tokenHash, UUID familyId, Instant expiresAt) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.familyId = familyId;
        this.expiresAt = expiresAt;
    }

    public boolean isActive(Instant now) {
        return !revoked && rotatedTo == null && expiresAt.isAfter(now);
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void revoke() {
        this.revoked = true;
    }

    public UUID getRotatedTo() {
        return rotatedTo;
    }

    public void rotateTo(UUID newTokenId) {
        this.rotatedTo = newTokenId;
    }
}
