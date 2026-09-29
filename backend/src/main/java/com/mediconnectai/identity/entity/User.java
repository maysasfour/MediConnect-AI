package com.mediconnectai.identity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;

public record User(String id, String fullName, String email, @JsonIgnore String passwordHash,
                   String role, boolean active, Instant createdAt) {
}
