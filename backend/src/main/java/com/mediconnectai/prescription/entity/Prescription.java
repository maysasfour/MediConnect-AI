package com.mediconnectai.prescription.entity;
import java.time.LocalDate;
import java.util.List;
public record Prescription(String id, String patientId, String doctorName, LocalDate prescribedOn, String status, List<PrescriptionItem> items) {
    public Prescription { items = items == null ? List.of() : List.copyOf(items); }
}
