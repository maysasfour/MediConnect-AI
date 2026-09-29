package com.mediconnectai.medicalrecord.service;

import com.mediconnectai.medicalrecord.entity.Encounter;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {
    private final Map<String, Encounter> encounters = new ConcurrentHashMap<>();

    public MedicalRecordService() {
        Encounter encounter = new Encounter("e-701", "p-1002", "Dr. Lina Haddad",
            LocalDateTime.now().minusDays(12), "Follow-up", "Blood pressure improved after medication adjustment.");
        encounters.put(encounter.id(), encounter);
    }

    public List<Encounter> findByPatient(String patientId) {
        return encounters.values().stream().filter(item -> item.patientId().equals(patientId))
            .sorted(Comparator.comparing(Encounter::occurredAt).reversed()).toList();
    }

    public Encounter create(Encounter request) {
        String id = "e-" + UUID.randomUUID().toString().substring(0, 8);
        Encounter encounter = new Encounter(id, request.patientId(), request.doctorName(),
            request.occurredAt() == null ? LocalDateTime.now() : request.occurredAt(), request.type(), request.summary());
        encounters.put(id, encounter);
        return encounter;
    }
}
