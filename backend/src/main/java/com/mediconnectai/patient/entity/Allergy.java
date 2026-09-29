package com.mediconnectai.patient.entity;

public record Allergy(String id, String patientId, String substance, String severity, String reaction) {
}
