package com.mediconnectai.appointment.service;

import com.mediconnectai.appointment.dto.AppointmentRequest;
import com.mediconnectai.appointment.dto.AppointmentResponse;
import com.mediconnectai.appointment.entity.Appointment;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {
    private final Map<String, Appointment> appointments = new ConcurrentHashMap<>();

    public AppointmentService() {
        Appointment first = new Appointment("a-501", "p-1002", "Maya Saleh", "Dr. Lina Haddad",
            LocalDateTime.now().withHour(10).withMinute(30).withSecond(0).withNano(0), "Follow-up", "Confirmed");
        appointments.put(first.id(), first);
    }

    public List<AppointmentResponse> findAll() {
        return appointments.values().stream().sorted(Comparator.comparing(Appointment::startsAt))
            .map(this::toResponse).toList();
    }

    public AppointmentResponse book(AppointmentRequest request) {
        String id = "a-" + UUID.randomUUID().toString().substring(0, 8);
        Appointment appointment = new Appointment(id, request.patientId(), request.patientId(), request.doctorName(),
            request.startsAt(), request.visitType(), "Pending");
        appointments.put(id, appointment);
        return toResponse(appointment);
    }

    public AppointmentResponse changeStatus(String id, String status) {
        Appointment current = requireAppointment(id);
        Appointment updated = new Appointment(current.id(), current.patientId(), current.patientName(),
            current.doctorName(), current.startsAt(), current.visitType(), status);
        appointments.put(id, updated);
        return toResponse(updated);
    }

    private Appointment requireAppointment(String id) {
        Appointment appointment = appointments.get(id);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found: " + id);
        }
        return appointment;
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(appointment.id(), appointment.patientId(), appointment.patientName(),
            appointment.doctorName(), appointment.startsAt(), appointment.visitType(), appointment.status());
    }
}
