package com.mediconnectai.security;
import java.util.Optional;
import org.springframework.stereotype.Component;
@Component
public class JwtAuthenticationFilter {
    private final JwtTokenProvider tokens;
    public JwtAuthenticationFilter(JwtTokenProvider tokens) { this.tokens = tokens; }
    public Optional<UserPrincipal> authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) return Optional.empty();
        try { return Optional.of(tokens.verify(authorizationHeader.substring(7))); }
        catch (IllegalArgumentException exception) { return Optional.empty(); }
    }
}
