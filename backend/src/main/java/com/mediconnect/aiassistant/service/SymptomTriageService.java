package com.mediconnect.aiassistant.service;

import com.mediconnect.aiassistant.domain.CareLevel;
import com.mediconnect.aiassistant.provider.AiModelProvider;
import com.mediconnect.aiassistant.provider.AiModelProvider.ModelRequest;
import com.mediconnect.aiassistant.provider.AiModelProvider.ModelResponse;
import com.mediconnect.aiassistant.service.RedFlagDetector.RedFlag;
import com.mediconnect.shared.error.DomainExceptions.ForbiddenOperationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Core, provider-agnostic orchestration for one assistant turn. Deliberately
 * free of persistence so the safety rules can be unit-tested in isolation.
 *
 * <p>The ordering of the guards is itself a safety property and must not change:
 * <ol>
 *   <li>Consent is required before any processing.</li>
 *   <li>Red-flag detection runs <b>before</b> the model is ever called; on a
 *       match the turn is interrupted with emergency guidance and the text is
 *       never forwarded to a provider.</li>
 *   <li>User input is sanitized against prompt injection.</li>
 *   <li>Model output is validated; anything that looks like diagnosis or
 *       prescribing is discarded in favour of a safe fallback.</li>
 * </ol>
 */
@Service
public class SymptomTriageService {

    private static final Logger log = LoggerFactory.getLogger(SymptomTriageService.class);

    private final RedFlagDetector redFlagDetector;
    private final PromptSanitizer sanitizer;
    private final AiModelProvider provider;

    public SymptomTriageService(RedFlagDetector redFlagDetector, PromptSanitizer sanitizer,
                                AiModelProvider provider) {
        this.redFlagDetector = redFlagDetector;
        this.sanitizer = sanitizer;
        this.provider = provider;
    }

    /**
     * @param consentGranted whether the patient has consented to symptom processing
     * @param userMessage    the raw patient message
     * @param language       "en" or "ar"
     * @param priorTurns     prior assistant/user turns for context (already stored)
     */
    public TriageResult assess(boolean consentGranted, String userMessage,
                               String language, List<String> priorTurns) {
        if (!consentGranted) {
            throw new ForbiddenOperationException(
                    "Consent is required before symptom information can be processed");
        }

        // 1. Red flags first — never send an emergency to an external model.
        var redFlag = redFlagDetector.detect(userMessage);
        if (redFlag.isPresent()) {
            RedFlag flag = redFlag.get();
            log.info("Red flag '{}' detected; interrupting chat with emergency guidance", flag.category());
            return TriageResult.emergency(flag.category(), emergencyMessage(language, flag.category()));
        }

        // 2. Sanitize input against prompt injection before it reaches the model.
        var sanitized = sanitizer.sanitizeInput(userMessage);
        if (sanitized.injectionDetected()) {
            log.warn("Possible prompt-injection attempt neutralized in assistant input");
        }

        // 3. Call the (swappable, de-identified) provider.
        ModelResponse response = provider.analyzeSymptoms(
                new ModelRequest(sanitized.text(), language, priorTurns));

        // 4. Validate model output. If it strays into diagnosis/prescribing, fall back.
        String assistantMessage = response.assistantMessage();
        if (!sanitizer.isOutputSafe(assistantMessage)) {
            log.warn("Model output failed safety validation; substituting safe fallback");
            assistantMessage = safeFallback(language);
        }

        return new TriageResult(
                false,
                response.careLevel(),
                response.suggestedSpecialty(),
                response.followUpQuestions(),
                response.summaryPoints(),
                assistantMessage,
                null);
    }

    private String emergencyMessage(String language, String category) {
        if ("ar".equalsIgnoreCase(language)) {
            return "قد تكون هذه حالة طارئة. يرجى الاتصال بالطوارئ (911 في الأردن) "
                    + "أو التوجه إلى أقرب قسم طوارئ فوراً. هذه ليست نصيحة تشخيصية.";
        }
        return "This may be an emergency. Please call emergency services (911 in Jordan) "
                + "or go to the nearest emergency department immediately. "
                + "This is not a diagnosis.";
    }

    private String safeFallback(String language) {
        if ("ar".equalsIgnoreCase(language)) {
            return "لا يمكنني تقديم تشخيص أو وصف دواء. يمكنني فقط مشاركة معلومات عامة "
                    + "ومساعدتك في تحديد نوع الرعاية المناسبة. يرجى مراجعة الطبيب.";
        }
        return "I can't provide a diagnosis or prescribe medication. I can only share general "
                + "information and help you decide what kind of care may be appropriate. "
                + "Please consult a clinician.";
    }

    /** Immutable result of one triage turn. */
    public record TriageResult(
            boolean emergency,
            CareLevel careLevel,
            String suggestedSpecialty,
            List<String> followUpQuestions,
            List<String> summaryPoints,
            String assistantMessage,
            String redFlagCategory) {

        static TriageResult emergency(String category, String message) {
            return new TriageResult(true, CareLevel.EMERGENCY, "Emergency",
                    List.of(), List.of(), message, category);
        }
    }
}
