package com.mediconnectai.privacy.entity;
import java.time.Instant;
public record PrivacyRequest(String id, String patientId, String type, String status, Instant requestedAt, Instant completedAt) {}
