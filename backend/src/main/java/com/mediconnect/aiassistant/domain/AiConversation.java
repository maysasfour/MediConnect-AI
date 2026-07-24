package com.mediconnect.aiassistant.domain;

import com.mediconnect.shared.domain.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * A single AI symptom-assistant conversation. Bound to a patient and clinic.
 * Consent to process symptom text is recorded on the conversation itself; no
 * message is stored or sent to a provider before consent is granted.
 */
@Entity
@Table(name = "ai_conversation")
public class AiConversation extends TenantAwareEntity {

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private String language = "en";

    @Column(name = "consent_granted", nullable = false)
    private boolean consentGranted = false;

    @Column(name = "consent_granted_at")
    private Instant consentGrantedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    protected AiConversation() {
    }

    public AiConversation(UUID clinicId, UUID patientId, String language) {
        setClinicId(clinicId);
        this.patientId = patientId;
        this.language = language;
    }

    public void grantConsent() {
        this.consentGranted = true;
        this.consentGrantedAt = Instant.now();
    }

    public void softDelete() {
        this.deleted = true;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isConsentGranted() {
        return consentGranted;
    }

    public Instant getConsentGrantedAt() {
        return consentGrantedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
