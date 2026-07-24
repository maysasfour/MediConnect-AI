package com.mediconnect.medicalrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mediconnect.medicalrecord.domain.ClinicalNote;
import com.mediconnect.medicalrecord.domain.ClinicalNoteStatus;
import com.mediconnect.shared.error.DomainExceptions.BusinessRuleException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClinicalNoteTest {

    private ClinicalNote draft() {
        return new ClinicalNote(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Patient reports headache for 2 days.");
    }

    @Test
    @DisplayName("A draft note can be edited")
    void draftIsEditable() {
        ClinicalNote note = draft();
        note.editDraft("Updated body");
        assertThat(note.getBody()).isEqualTo("Updated body");
        assertThat(note.getStatus()).isEqualTo(ClinicalNoteStatus.DRAFT);
    }

    @Test
    @DisplayName("Signing makes the note immutable")
    void signingMakesImmutable() {
        ClinicalNote note = draft();
        UUID doctor = UUID.randomUUID();
        note.sign(doctor);

        assertThat(note.getStatus()).isEqualTo(ClinicalNoteStatus.SIGNED);
        assertThat(note.isImmutable()).isTrue();
        assertThat(note.getSignedBy()).isEqualTo(doctor);
        assertThat(note.getSignedAt()).isNotNull();
    }

    @Test
    @DisplayName("A signed note cannot be edited")
    void signedNoteCannotBeEdited() {
        ClinicalNote note = draft();
        note.sign(UUID.randomUUID());

        assertThatThrownBy(() -> note.editDraft("tampered"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cannot be edited");
    }

    @Test
    @DisplayName("An empty note cannot be signed")
    void cannotSignEmptyNote() {
        ClinicalNote note = new ClinicalNote(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "text");
        note.editDraft("   ");
        assertThatThrownBy(() -> note.sign(UUID.randomUUID()))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("A draft cannot be amended; it must be signed first")
    void draftCannotBeAmended() {
        ClinicalNote note = draft();
        assertThatThrownBy(() -> note.addAmendment(UUID.randomUUID(), "correction", "typo"))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("Amending a signed note preserves the original body and appends the amendment")
    void amendmentIsAppendOnly() {
        ClinicalNote note = draft();
        String originalBody = note.getBody();
        note.sign(UUID.randomUUID());

        UUID amender = UUID.randomUUID();
        note.addAmendment(amender, "Correction: headache for 3 days, not 2.", "Duration was mis-recorded");

        assertThat(note.getStatus()).isEqualTo(ClinicalNoteStatus.AMENDED);
        assertThat(note.getBody()).isEqualTo(originalBody); // original preserved
        assertThat(note.getAmendments()).hasSize(1);
        assertThat(note.getAmendments().get(0).getAuthorId()).isEqualTo(amender);
        assertThat(note.getAmendments().get(0).getReason()).isEqualTo("Duration was mis-recorded");
    }

    @Test
    @DisplayName("Amendments require a correction reason")
    void amendmentRequiresReason() {
        ClinicalNote note = draft();
        note.sign(UUID.randomUUID());
        assertThatThrownBy(() -> note.addAmendment(UUID.randomUUID(), "text", "  "))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("reason");
    }

    @Test
    @DisplayName("Multiple amendments accumulate")
    void multipleAmendments() {
        ClinicalNote note = draft();
        note.sign(UUID.randomUUID());
        note.addAmendment(UUID.randomUUID(), "first", "reason 1");
        note.addAmendment(UUID.randomUUID(), "second", "reason 2");
        assertThat(note.getAmendments()).hasSize(2);
    }
}
