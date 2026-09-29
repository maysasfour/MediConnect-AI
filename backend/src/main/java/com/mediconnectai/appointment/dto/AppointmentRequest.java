package com.mediconnectai.appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record AppointmentRequest(@NotBlank String patientId, @NotBlank String doctorName,
                                 @FutureOrPresent LocalDateTime startsAt, @NotBlank String visitType) {
}
