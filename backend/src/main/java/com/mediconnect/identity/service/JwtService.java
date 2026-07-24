package com.mediconnect.identity.service;

import com.mediconnect.identity.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Issues and verifies short-lived stateless access tokens (HS256). Refresh
 * tokens are opaque and tracked in the database (see {@code RefreshTokenService});
 * access tokens intentionally carry only the claims needed for authorization:
 * subject, tenant and roles.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final String issuer;

    public JwtService(
            @Value("${mediconnect.security.jwt.secret}") String secret,
            @Value("${mediconnect.security.jwt.access-ttl-seconds:900}") long accessTtlSeconds,
            @Value("${mediconnect.security.jwt.issuer:mediconnect}") String issuer) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTtlSeconds = accessTtlSeconds;
        this.issuer = issuer;
    }

    public String issueAccessToken(UUID userId, UUID clinicId, String email, Set<Role> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .claim("email", email)
                .claim("clinicId", clinicId == null ? null : clinicId.toString())
                .claim("roles", roles.stream().map(Enum::name).collect(Collectors.toList()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getAccessTtlSeconds() {
        return accessTtlSeconds;
    }
}
