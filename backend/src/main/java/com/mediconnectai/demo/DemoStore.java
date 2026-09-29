package com.mediconnectai.demo;

import com.mediconnectai.demo.DemoModels.Appointment;
import com.mediconnectai.demo.DemoModels.AppointmentRequest;
import com.mediconnectai.demo.DemoModels.DashboardStats;
import com.mediconnectai.demo.DemoModels.MedicalRecord;
import com.mediconnectai.demo.DemoModels.Patient;
import com.mediconnectai.demo.DemoModels.UserProfile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service
public class DemoStore {
    private final List<Patient> patients = new CopyOnWriteArrayList<>();
    private final List<Appointment> appointments = new CopyOnWriteArrayList<>();
    private final List<MedicalRecord> records = new CopyOnWriteArrayList<>();
    private final Map<String, UserProfile> users = Map.of(
        "doctor@mediconnect.ai", new UserProfile("u-doctor", "Dr. Lina Haddad", "doctor@mediconnect.ai", "DOCTOR"),
        "admin@mediconnect.ai", new UserProfile("u-admin", "Clinic Admin", "admin@mediconnect.ai", "ADMIN"),
        "patient@mediconnect.ai", new UserProfile("u-patient", "Omar Nasser", "patient@mediconnect.ai", "PATIENT")
    );

    public DemoStore() {
        patients.add(new Patient("p-1001", "Omar Nasser", 34, "Male", "+962790000101", "Medium",
            List.of("Asthma"), List.of("Penicillin")));
        patients.add(new Patient("p-1002", "Maya Saleh", 42, "Female", "+962790000102", "High",
            List.of("Hypertension", "Type 2 Diabetes"), List.of("None reported")));
        patients.add(new Patient("p-1003", "Yousef Karim", 28, "Male", "+962790000103", "Low",
            List.of("Seasonal allergies"), List.of("Ibuprofen")));

        appointments.add(new Appointment("a-501", "p-1002", "Maya Saleh", "Dr. Lina Haddad",
            LocalDateTime.now().withHour(10).withMinute(30).withSecond(0).withNano(0), "Follow-up", "Confirmed"));
        appointments.add(new Appointment("a-502", "p-1001", "Omar Nasser", "Dr. Samer Khoury",
            LocalDateTime.now().plusDays(1).withHour(13).withMinute(0).withSecond(0).withNano(0), "Respiratory consult", "Pending"));

        records.add(new MedicalRecord("r-701", "p-1002", LocalDate.now().minusDays(12), "Dr. Lina Haddad",
            "Hypertension review", "Blood pressure trending down after medication adjustment.",
            List.of("Amlodipine 5mg daily")));
        records.add(new MedicalRecord("r-702", "p-1001", LocalDate.now().minusDays(25), "Dr. Samer Khoury",
            "Asthma maintenance", "No acute distress. Reviewed inhaler technique.",
            List.of("Salbutamol inhaler as needed")));
    }

    public Optional<UserProfile> findUser(String email) {
        return Optional.ofNullable(users.get(email));
    }

    public List<Patient> patients() {
        return patients.stream()
            .sorted(Comparator.comparing(Patient::name))
            .toList();
    }

    public Optional<Patient> patient(String id) {
        return patients.stream().filter(patient -> patient.id().equals(id)).findFirst();
    }

    public List<Appointment> appointments() {
        return appointments.stream()
            .sorted(Comparator.comparing(Appointment::startsAt))
            .toList();
    }

    public Appointment book(AppointmentRequest request) {
        String patientName = patient(request.patientId()).map(Patient::name).orElse("New patient");
        Appointment appointment = new Appointment("a-" + UUID.randomUUID().toString().substring(0, 8),
            request.patientId(), patientName, request.doctorName(), request.startsAt(), request.type(), "Pending");
        appointments.add(appointment);
        return appointment;
    }

    public List<MedicalRecord> medicalRecords(String patientId) {
        return records.stream()
            .filter(record -> record.patientId().equals(patientId))
            .sorted(Comparator.comparing(MedicalRecord::date).reversed())
            .toList();
    }

    public DashboardStats stats() {
        LocalDate today = LocalDate.now();
        long appointmentsToday = appointments.stream()
            .filter(appointment -> appointment.startsAt().toLocalDate().equals(today))
            .count();
        long pending = appointments.stream()
            .filter(appointment -> appointment.status().equalsIgnoreCase("Pending"))
            .count();
        long highRisk = patients.stream()
            .filter(patient -> patient.riskLevel().equalsIgnoreCase("High"))
            .count();
        return new DashboardStats(patients.size(), appointmentsToday, pending, highRisk);
    }

    public List<String> doctors() {
        return new ArrayList<>(List.of("Dr. Lina Haddad", "Dr. Samer Khoury", "Dr. Reem Mansour"));
    }
}
