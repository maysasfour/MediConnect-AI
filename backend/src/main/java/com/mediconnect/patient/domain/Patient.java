package com.mediconnect.patient.domain;

import com.mediconnect.shared.domain.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A patient record, scoped to a single clinic. Direct identifiers (national id,
 * names, contact details) live here and are deliberately excluded from any data
 * sent to an AI provider (data minimization).
 */
@Entity
@Table(name = "patient")
public class Patient extends TenantAwareEntity {

    @Column(name = "medical_record_number", nullable = false)
    private String medicalRecordNumber;

    @Column(name = "full_name_en", nullable = false)
    private String fullNameEn;

    @Column(name = "full_name_ar")
    private String fullNameAr;

    @Column(name = "national_id")
    private String nationalId;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String sex;

    @Column(name = "phone_e164")
    private String phoneE164;

    private String email;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "preferred_language", nullable = false)
    private String preferredLanguage = "ar";

    @Column(name = "linked_user_id")
    private UUID linkedUserId;

    protected Patient() {
    }

    public Patient(UUID clinicId, String medicalRecordNumber, String fullNameEn,
                   LocalDate dateOfBirth, String sex) {
        setClinicId(clinicId);
        this.medicalRecordNumber = medicalRecordNumber;
        this.fullNameEn = fullNameEn;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public String getFullNameEn() {
        return fullNameEn;
    }

    public void setFullNameEn(String fullNameEn) {
        this.fullNameEn = fullNameEn;
    }

    public String getFullNameAr() {
        return fullNameAr;
    }

    public void setFullNameAr(String fullNameAr) {
        this.fullNameAr = fullNameAr;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public String getPhoneE164() {
        return phoneE164;
    }

    public void setPhoneE164(String phoneE164) {
        this.phoneE164 = phoneE164;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public UUID getLinkedUserId() {
        return linkedUserId;
    }

    public void setLinkedUserId(UUID linkedUserId) {
        this.linkedUserId = linkedUserId;
    }
}
