package com.mediconnectai.patient.entity;

import java.time.Instant;
import java.util.List;

public record Patient(String id, String fullName, int age, String gender, String phone, String riskLevel,
                      List<String> conditions, List<String> allergies, Instant createdAt) {
    public Patient {
        conditions = conditions == null ? List.of() : List.copyOf(conditions);
        allergies = allergies == null ? List.of() : List.copyOf(allergies);
    }
}
