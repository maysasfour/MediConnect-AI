package com.mediconnectai.patient.service;

import com.mediconnectai.patient.dto.PatientRequest;
import com.mediconnectai.patient.dto.PatientResponse;
import com.mediconnectai.patient.entity.Patient;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    private final Map<String, Patient> patients = new ConcurrentHashMap<>();

    public PatientService() {
        saveSeed(new Patient("p-1001", "Omar Nasser", 34, "Male", "+962790000101", "Medium",
            List.of("Asthma"), List.of("Penicillin"), Instant.now()));
        saveSeed(new Patient("p-1002", "Maya Saleh", 42, "Female", "+962790000102", "High",
            List.of("Hypertension", "Type 2 Diabetes"), List.of("None reported"), Instant.now()));
        saveSeed(new Patient("p-1003", "Yousef Karim", 28, "Male", "+962790000103", "Low",
            List.of("Seasonal allergies"), List.of("Ibuprofen"), Instant.now()));
    }

    public List<PatientResponse> findAll() {
        return patients.values().stream().sorted(Comparator.comparing(Patient::fullName)).map(this::toResponse).toList();
    }

    public PatientResponse findById(String id) {
        return toResponse(requirePatient(id));
    }

    public PatientResponse create(PatientRequest request) {
        String id = "p-" + UUID.randomUUID().toString().substring(0, 8);
        Patient patient = fromRequest(id, request, Instant.now());
        patients.put(id, patient);
        return toResponse(patient);
    }

    public PatientResponse update(String id, PatientRequest request) {
        Patient current = requirePatient(id);
        Patient patient = fromRequest(id, request, current.createdAt());
        patients.put(id, patient);
        return toResponse(patient);
    }

    public void delete(String id) {
        if (patients.remove(id) == null) {
            throw new IllegalArgumentException("Patient not found: " + id);
        }
    }

    private Patient requirePatient(String id) {
        Patient patient = patients.get(id);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found: " + id);
        }
        return patient;
    }

    private void saveSeed(Patient patient) {
        patients.put(patient.id(), patient);
    }

    private Patient fromRequest(String id, PatientRequest request, Instant createdAt) {
        return new Patient(id, request.fullName(), request.age(), request.gender(), request.phone(),
            request.riskLevel() == null ? "Low" : request.riskLevel(), request.conditions(), request.allergies(), createdAt);
    }

    private PatientResponse toResponse(Patient patient) {
        return new PatientResponse(patient.id(), patient.fullName(), patient.age(), patient.gender(), patient.phone(),
            patient.riskLevel(), patient.conditions(), patient.allergies());
    }
}
