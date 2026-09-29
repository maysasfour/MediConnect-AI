package com.mediconnectai.medicalrecord.entity;

import java.time.LocalDateTime;

public record Encounter(String id, String patientId, String doctorName, LocalDateTime occurredAt,
                        String type, String summary) {
}
