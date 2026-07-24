package com.mediconnect.medicalrecord.domain;

public enum ClinicalNoteStatus {
    /** Editable working state. */
    DRAFT,
    /** Signed and immutable. Corrections are made only via append-only amendments. */
    SIGNED,
    /** Signed and subsequently amended; the original signed content is preserved. */
    AMENDED
}
