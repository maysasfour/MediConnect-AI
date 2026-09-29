package com.mediconnectai.patient.dto;

import java.util.List;

public record PatientResponse(String id, String fullName, int age, String gender, String phone,
                              String riskLevel, List<String> conditions, List<String> allergies) {
}
