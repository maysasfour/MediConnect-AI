package com.mediconnectai.patient.entity;

import java.time.LocalDate;

public record MedicalCondition(String id, String patientId, String name, LocalDate diagnosedOn, String status) {
}
