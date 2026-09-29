package com.mediconnectai.medicalrecord.entity;

import java.time.Instant;

public record Document(String id, String patientId, String name, String contentType, String location,
                       Instant uploadedAt) {
}
