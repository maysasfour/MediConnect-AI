package com.mediconnectai.privacy.entity;
import java.time.Instant;
public record Consent(String id, String patientId, String purpose, boolean granted, Instant updatedAt) {}
