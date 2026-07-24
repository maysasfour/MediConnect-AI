package com.mediconnect.identity.service;

import com.mediconnect.identity.domain.RefreshToken;
import com.mediconnect.identity.repo.RefreshTokenRepository;
import com.mediconnect.shared.error.DomainExceptions.ForbiddenOperationException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages the rotating refresh-token family. Raw tokens are returned to the
 * client but only their SHA-256 hashes are stored. Presenting a token that has
 * already been rotated (a replay) is treated as theft and revokes the entire
 * family.
 */
@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository repository;
    private final Duration ttl;

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${mediconnect.security.jwt.refresh-ttl-days:14}") long refreshTtlDays) {
        this.repository = repository;
        this.ttl = Duration.ofDays(refreshTtlDays);
    }

    /** Issues a brand-new token family for a fresh login. */
    @Transactional
    public String issueNewFamily(UUID userId) {
        return persist(userId, UUID.randomUUID());
    }

    /**
     * Validates a presented refresh token and rotates it. Returns a new raw
     * token in the same family. Reuse of a rotated token revokes the family.
     */
    @Transactional
    public RotationResult rotate(String rawToken) {
        String hash = hash(rawToken);
        RefreshToken existing = repository.findByTokenHash(hash)
                .orElseThrow(() -> new ForbiddenOperationException("Invalid refresh token"));

        Instant now = Instant.now();
        if (existing.getRotatedTo() != null || existing.isRevoked()) {
            // Replay of an already-used token: revoke the whole family.
            log.warn("Refresh token reuse detected for family {} — revoking family", existing.getFamilyId());
            repository.revokeFamily(existing.getFamilyId());
            throw new ForbiddenOperationException("Refresh token has already been used");
        }
        if (!existing.isActive(now)) {
            throw new ForbiddenOperationException("Refresh token expired");
        }

        String newRaw = generateRaw();
        RefreshToken rotated = new RefreshToken(existing.getUserId(), hash(newRaw),
                existing.getFamilyId(), now.plus(ttl));
        repository.save(rotated);
        existing.rotateTo(rotated.getId());
        existing.revoke();
        return new RotationResult(existing.getUserId(), newRaw);
    }

    @Transactional
    public void revokeAllForUser(UUID userId) {
        repository.revokeAllForUser(userId);
    }

    private String persist(UUID userId, UUID familyId) {
        String raw = generateRaw();
        RefreshToken token = new RefreshToken(userId, hash(raw), familyId, Instant.now().plus(ttl));
        repository.save(token);
        return raw;
    }

    private String generateRaw() {
        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] out = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    public record RotationResult(UUID userId, String rawToken) {
    }
}
