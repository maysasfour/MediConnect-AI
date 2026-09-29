package com.mediconnectai.security;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class JwtTokenProvider {
    private final byte[] secret;
    public JwtTokenProvider(@Value("${app.jwt.secret:mediconnect-local-development-secret}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }
    public String create(String userId, String role) { return createSigned(userId + "|" + role + "|" + Instant.now().plus(1, ChronoUnit.HOURS).getEpochSecond()); }
    public String createRefreshToken(String userId) { return createSigned(userId + "|REFRESH|" + Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond()); }
    public UserPrincipal verify(String token) {
        int separator = token.lastIndexOf('.');
        if (separator < 1) throw new IllegalArgumentException("Malformed token");
        String payload = token.substring(0, separator);
        if (!constantTimeEquals(sign(payload), token.substring(separator + 1))) throw new IllegalArgumentException("Invalid token signature");
        String[] parts = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8).split("\\|");
        if (parts.length != 3 || Long.parseLong(parts[2]) <= Instant.now().getEpochSecond()) throw new IllegalArgumentException("Expired token");
        return new UserPrincipal(parts[0], null, parts[1], java.util.Set.of());
    }
    private String createSigned(String value) {
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
        return payload + "." + sign(payload);
    }
    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) { throw new IllegalStateException("Token signing failed", exception); }
    }
    private boolean constantTimeEquals(String left, String right) {
        return java.security.MessageDigest.isEqual(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }
}
