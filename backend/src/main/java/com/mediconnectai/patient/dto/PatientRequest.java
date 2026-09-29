package com.mediconnectai.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record PatientRequest(@NotBlank String fullName, @PositiveOrZero int age, @NotBlank String gender,
                             @NotBlank String phone, String riskLevel, List<String> conditions,
                             List<String> allergies) {
}
