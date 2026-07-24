package com.mediconnect.medicalrecord.domain;

import com.mediconnect.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * An append-only correction to a signed {@link ClinicalNote}. Amendments are
 * never edited or deleted; each records who made the correction, and why.
 */
@Entity
@Table(name = "clinical_note_amendment")
public class ClinicalNoteAmendment extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "note_id", nullable = false, updatable = false)
    private ClinicalNote note;

    @Column(name = "author_id", nullable = false, updatable = false)
    private UUID authorId;

    @Column(name = "text", nullable = false, updatable = false, length = 20000)
    private String text;

    @Column(name = "reason", nullable = false, updatable = false)
    private String reason;

    protected ClinicalNoteAmendment() {
    }

    ClinicalNoteAmendment(ClinicalNote note, UUID authorId, String text, String reason) {
        this.note = note;
        this.authorId = authorId;
        this.text = text;
        this.reason = reason;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getText() {
        return text;
    }

    public String getReason() {
        return reason;
    }
}
