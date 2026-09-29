package com.mediconnectai.patient.entity;

import java.time.LocalDate;

public record InsurancePolicy(String id, String patientId, String provider, String policyNumber,
                              LocalDate validUntil) {
}
