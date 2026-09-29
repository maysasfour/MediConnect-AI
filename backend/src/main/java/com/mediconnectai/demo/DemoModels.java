package com.mediconnectai.demo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class DemoModels {
    private DemoModels() {
    }

    public record LoginRequest(@Email String email, @NotBlank String password) {
    }

    public record AuthResponse(String token, UserProfile user) {
    }

    public record UserProfile(String id, String name, String email, String role) {
    }

    public record Patient(String id, String name, int age, String gender, String phone, String riskLevel,
                          List<String> conditions, List<String> allergies) {
    }

    public record Appointment(String id, String patientId, String patientName, String doctorName,
                              LocalDateTime startsAt, String type, String status) {
    }

    public record AppointmentRequest(@NotBlank String patientId, @NotBlank String doctorName,
                                     @NotNull LocalDateTime startsAt, @NotBlank String type) {
    }

    public record MedicalRecord(String id, String patientId, LocalDate date, String doctorName,
                                String diagnosis, String notes, List<String> prescriptions) {
    }

    public record AiRequest(@NotBlank String message, String patientId) {
    }

    public record AiResponse(String summary, List<String> safetyNotes, List<String> suggestedNextSteps) {
    }

    public record DashboardStats(long patients, long appointmentsToday, long pendingAppointments,
                                 long highRiskPatients) {
    }
}
