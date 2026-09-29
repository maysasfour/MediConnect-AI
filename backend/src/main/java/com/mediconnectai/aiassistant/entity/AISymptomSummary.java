package com.mediconnectai.aiassistant.entity;
import java.time.Instant;
import java.util.List;
public record AISymptomSummary(String id, String patientId, String summary, String urgency, List<String> safetyNotes, List<String> nextSteps, Instant createdAt) {
    public AISymptomSummary {
        safetyNotes = safetyNotes == null ? List.of() : List.copyOf(safetyNotes);
        nextSteps = nextSteps == null ? List.of() : List.copyOf(nextSteps);
    }
}
