package com.mediconnectai.appointment.repository;
import com.mediconnectai.appointment.entity.Appointment;
import java.util.List;
import java.util.Optional;
public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(String id);
    List<Appointment> findAll();
    List<Appointment> findByPatientId(String patientId);
}
