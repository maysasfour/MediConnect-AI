package com.mediconnectai.prescription.repository;
import com.mediconnectai.prescription.entity.Prescription;
import java.util.List;
import java.util.Optional;
public interface PrescriptionRepository {
    Prescription save(Prescription prescription);
    Optional<Prescription> findById(String id);
    List<Prescription> findByPatientId(String patientId);
}
