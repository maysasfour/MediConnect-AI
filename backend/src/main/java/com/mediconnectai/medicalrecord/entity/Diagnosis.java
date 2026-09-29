package com.mediconnectai.medicalrecord.entity;

import java.time.LocalDate;

public record Diagnosis(String id, String encounterId, String code, String description, LocalDate diagnosedOn) {
}
