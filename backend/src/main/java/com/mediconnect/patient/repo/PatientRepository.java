package com.mediconnect.patient.repo;

import com.mediconnect.patient.domain.Patient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Patient persistence. Every finder is clinic-scoped: there is deliberately no
 * {@code findById(UUID)} in service use that bypasses the tenant, so an id from
 * another clinic cannot resolve to a patient.
 */
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByIdAndClinicId(UUID id, UUID clinicId);

    Page<Patient> findByClinicId(UUID clinicId, Pageable pageable);

    // Simple duplicate-detection support: same clinic, same national id.
    List<Patient> findByClinicIdAndNationalId(UUID clinicId, String nationalId);
}
