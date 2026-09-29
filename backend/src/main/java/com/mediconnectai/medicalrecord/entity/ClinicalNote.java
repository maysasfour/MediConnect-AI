package com.mediconnectai.medicalrecord.entity;

import java.time.Instant;

public record ClinicalNote(String id, String encounterId, String author, String content, Instant createdAt) {
}
