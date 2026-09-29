package com.mediconnectai.appointment.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(String id, String patientId, String patientName, String doctorName,
                                  LocalDateTime startsAt, String visitType, String status) {
}
