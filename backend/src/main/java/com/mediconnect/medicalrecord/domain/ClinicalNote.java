package com.mediconnect.medicalrecord.domain;

import com.mediconnect.shared.domain.TenantAwareEntity;
import com.mediconnect.shared.error.DomainExceptions.BusinessRuleException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A clinical note attached to an encounter. Enforces the medico-legal rule that
 * a <b>signed note is immutable</b>: once signed, its body cannot be edited.
 * Corrections are recorded as append-only {@link ClinicalNoteAmendment}s that
 * capture the author, timestamp and reason, and move the note to {@code AMENDED}
 * while preserving the original signed content.
 */
@Entity
@Table(name = "clinical_note")
public class ClinicalNote extends TenantAwareEntity {

    @Column(name = "encounter_id", nullable = false)
    private UUID encounterId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "body", nullable = false, length = 20000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClinicalNoteStatus status = ClinicalNoteStatus.DRAFT;

    @Column(name = "signed_by")
    private UUID signedBy;

    @Column(name = "signed_at")
    private Instant signedAt;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<ClinicalNoteAmendment> amendments = new ArrayList<>();

    protected ClinicalNote() {
    }

    public ClinicalNote(UUID clinicId, UUID encounterId, UUID authorId, String body) {
        setClinicId(clinicId);
        this.encounterId = encounterId;
        this.authorId = authorId;
        this.body = body;
    }

    /** Edit the draft body. Rejected once the note is signed. */
    public void editDraft(String newBody) {
        if (status != ClinicalNoteStatus.DRAFT) {
            throw new BusinessRuleException("NOTE_IMMUTABLE",
                    "A signed clinical note cannot be edited; add an amendment instead");
        }
        this.body = newBody;
    }

    /** Sign the note, making it immutable. */
    public void sign(UUID signedBy) {
        if (status != ClinicalNoteStatus.DRAFT) {
            throw new BusinessRuleException("NOTE_ALREADY_SIGNED",
                    "Only a draft note can be signed");
        }
        if (body == null || body.isBlank()) {
            throw new BusinessRuleException("NOTE_EMPTY", "Cannot sign an empty note");
        }
        this.status = ClinicalNoteStatus.SIGNED;
        this.signedBy = signedBy;
        this.signedAt = Instant.now();
    }

    /**
     * Append a correction to a signed note. Does not alter the signed body; the
     * amendment carries the correction and the note becomes {@code AMENDED}.
     */
    public ClinicalNoteAmendment addAmendment(UUID authorId, String text, String reason) {
        if (status == ClinicalNoteStatus.DRAFT) {
            throw new BusinessRuleException("NOTE_NOT_SIGNED",
                    "Only a signed note can be amended; edit the draft instead");
        }
        if (text == null || text.isBlank()) {
            throw new BusinessRuleException("AMENDMENT_EMPTY", "Amendment text is required");
        }
        if (reason == null || reason.isBlank()) {
            throw new BusinessRuleException("AMENDMENT_REASON_REQUIRED",
                    "A correction reason is required");
        }
        ClinicalNoteAmendment amendment = new ClinicalNoteAmendment(this, authorId, text, reason);
        amendments.add(amendment);
        this.status = ClinicalNoteStatus.AMENDED;
        return amendment;
    }

    public boolean isImmutable() {
        return status == ClinicalNoteStatus.SIGNED || status == ClinicalNoteStatus.AMENDED;
    }

    public UUID getEncounterId() {
        return encounterId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getBody() {
        return body;
    }

    public ClinicalNoteStatus getStatus() {
        return status;
    }

    public UUID getSignedBy() {
        return signedBy;
    }

    public Instant getSignedAt() {
        return signedAt;
    }

    public List<ClinicalNoteAmendment> getAmendments() {
        return Collections.unmodifiableList(amendments);
    }
}
