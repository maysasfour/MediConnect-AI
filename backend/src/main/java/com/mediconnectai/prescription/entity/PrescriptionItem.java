package com.mediconnectai.prescription.entity;
public record PrescriptionItem(String id, String medicationName, String dosage, String frequency, int durationDays, String instructions) {}
