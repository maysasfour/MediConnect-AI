package com.mediconnectai.appointment.entity;
import java.time.Instant;
public record AppointmentStatusHistory(String id, String appointmentId, String fromStatus, String toStatus, String changedBy, Instant changedAt) {}
