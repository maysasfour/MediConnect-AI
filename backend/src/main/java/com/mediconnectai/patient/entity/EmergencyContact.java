package com.mediconnectai.patient.entity;

public record EmergencyContact(String id, String patientId, String name, String relationship, String phone) {
}
