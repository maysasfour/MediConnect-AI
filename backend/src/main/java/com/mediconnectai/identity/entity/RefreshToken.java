package com.mediconnectai.identity.entity;

import java.time.Instant;

public record RefreshToken(String token, String userId, Instant expiresAt, boolean revoked) {
    public boolean isValid() {
        return !revoked && expiresAt.isAfter(Instant.now());
    }
}
