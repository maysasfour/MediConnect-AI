package com.mediconnectai.audit.entity;
import java.time.Instant;
import java.util.Map;
public record AuditLog(String id, String actor, String action, String resource, Instant occurredAt, Map<String, String> details) {
    public AuditLog { details = details == null ? Map.of() : Map.copyOf(details); }
}
