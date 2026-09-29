package com.mediconnectai.identity.entity;

import java.util.Set;

public record Role(String id, String name, Set<String> permissions) {
    public Role {
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }
}
