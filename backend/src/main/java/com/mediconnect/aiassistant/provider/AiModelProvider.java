package com.mediconnect.aiassistant.provider;

import com.mediconnect.aiassistant.domain.CareLevel;
import java.util.List;

/**
 * Provider abstraction so the underlying model (mock, self-hosted, or a
 * third-party API) can be swapped without touching orchestration or safety
 * logic. Implementations receive only minimized, de-identified text — never
 * direct patient identifiers.
 */
public interface AiModelProvider {

    String name();

    ModelResponse analyzeSymptoms(ModelRequest request);

    /** Minimized request. {@code language} is "en" or "ar". No identifiers. */
    record ModelRequest(String sanitizedText, String language, List<String> priorTurns) {
    }

    /**
     * Structured, non-diagnostic result. {@code suggestedSpecialty} is a routing
     * hint, {@code summaryPoints} a neutral symptom summary for doctor review.
     */
    record ModelResponse(
            CareLevel careLevel,
            String suggestedSpecialty,
            List<String> followUpQuestions,
            List<String> summaryPoints,
            String assistantMessage) {
    }
}
