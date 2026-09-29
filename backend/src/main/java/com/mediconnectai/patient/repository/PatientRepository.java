package com.mediconnectai.patient.repository;

import com.mediconnectai.patient.entity.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(String id);
    List<Patient> findAll();
    boolean deleteById(String id);
}
