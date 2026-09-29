package com.mediconnectai.aiassistant.service;
import com.mediconnectai.aiassistant.entity.AISymptomSummary;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
@Service
public class AIService implements AIProvider {
    public AISymptomSummary summarize(String patientId, String symptoms) {
        String text = symptoms == null ? "" : symptoms.trim();
        String normalized = text.toLowerCase(Locale.ROOT);
        boolean urgent = List.of("chest pain", "cannot breathe", "unconscious", "severe bleeding").stream().anyMatch(normalized::contains);
        return new AISymptomSummary("ai-" + UUID.randomUUID().toString().substring(0, 8), patientId,
            text.isBlank() ? "No symptoms were provided." : "Patient-reported symptoms: " + text,
            urgent ? "Emergency" : "Clinical review",
            List.of("This assistant does not diagnose conditions.", "Seek emergency care for severe or rapidly worsening symptoms."),
            urgent ? List.of("Contact emergency services now.") : List.of("Record symptom duration and severity.", "Arrange a clinician review if symptoms persist."), Instant.now());
    }
}
