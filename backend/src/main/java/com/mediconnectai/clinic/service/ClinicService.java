package com.mediconnectai.clinic.service;

import com.mediconnectai.clinic.entity.Clinic;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClinicService {
    private final List<Clinic> clinics = List.of(
        new Clinic("clinic-1", "MediConnect Amman", "+96265000000", "care@mediconnect.ai", "Asia/Amman")
    );

    public List<Clinic> findAll() {
        return clinics;
    }

    public Clinic findById(String id) {
        return clinics.stream().filter(clinic -> clinic.id().equals(id)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Clinic not found: " + id));
    }
}
