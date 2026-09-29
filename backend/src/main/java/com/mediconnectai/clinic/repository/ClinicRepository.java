package com.mediconnectai.clinic.repository;

import com.mediconnectai.clinic.entity.Clinic;
import java.util.List;
import java.util.Optional;

public interface ClinicRepository {
    Clinic save(Clinic clinic);
    Optional<Clinic> findById(String id);
    List<Clinic> findAll();
}
