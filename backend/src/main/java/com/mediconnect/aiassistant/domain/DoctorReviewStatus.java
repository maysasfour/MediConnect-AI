package com.mediconnect.aiassistant.domain;

/**
 * Review state of an AI-generated symptom summary. A summary can only become
 * part of a patient's medical record after a doctor has explicitly reviewed and
 * accepted it — the AI never writes to the record automatically.
 */
public enum DoctorReviewStatus {
    PENDING_REVIEW,
    ACCEPTED,
    REJECTED
}
