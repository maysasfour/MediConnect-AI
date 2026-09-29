package com.mediconnectai.prescription.service;
import com.mediconnectai.prescription.entity.Prescription;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
@Service
public class PrescriptionService {
    private final Map<String, Prescription> prescriptions = new ConcurrentHashMap<>();
    public List<Prescription> findByPatient(String patientId) {
        return prescriptions.values().stream().filter(item -> item.patientId().equals(patientId)).sorted(Comparator.comparing(Prescription::prescribedOn).reversed()).toList();
    }
    public Prescription create(Prescription request) {
        String id = "rx-" + UUID.randomUUID().toString().substring(0, 8);
        Prescription prescription = new Prescription(id, request.patientId(), request.doctorName(), request.prescribedOn() == null ? LocalDate.now() : request.prescribedOn(), "Active", request.items());
        prescriptions.put(id, prescription);
        return prescription;
    }
}
