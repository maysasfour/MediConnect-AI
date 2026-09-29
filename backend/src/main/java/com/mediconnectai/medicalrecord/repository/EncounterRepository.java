package com.mediconnectai.medicalrecord.repository;
import com.mediconnectai.medicalrecord.entity.Encounter;
import java.util.List;
import java.util.Optional;
public interface EncounterRepository {
    Encounter save(Encounter encounter);
    Optional<Encounter> findById(String id);
    List<Encounter> findByPatientId(String patientId);
}
