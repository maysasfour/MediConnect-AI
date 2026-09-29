package com.mediconnectai.appointment.entity;
import java.time.LocalDateTime;
public record Appointment(String id, String patientId, String patientName, String doctorName, LocalDateTime startsAt, String visitType, String status) {}
