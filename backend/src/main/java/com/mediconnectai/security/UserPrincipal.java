package com.mediconnectai.security;
import java.util.Set;
public record UserPrincipal(String userId, String email, String role, Set<String> permissions) {
    public UserPrincipal { permissions = permissions == null ? Set.of() : Set.copyOf(permissions); }
}
