package com.mediconnect.aiassistant.provider;

import com.mediconnect.aiassistant.domain.CareLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Deterministic mock provider used for development, demos and tests. It performs
 * no real inference and calls no external service, so no patient data ever
 * leaves the system. It maps a few keyword families to a conservative care level
 * and specialty, and always returns non-diagnostic, information-only text.
 *
 * <p>Swap this for a real provider by supplying another {@link AiModelProvider}
 * bean and setting {@code mediconnect.ai.provider}.
 */
@Component
@ConditionalOnProperty(name = "mediconnect.ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockAiModelProvider implements AiModelProvider {

    @Override
    public String name() {
        return "mock";
    }

    @Override
    public ModelResponse analyzeSymptoms(ModelRequest request) {
        String text = request.sanitizedText().toLowerCase(Locale.ROOT);
        boolean arabic = "ar".equalsIgnoreCase(request.language());

        CareLevel careLevel = CareLevel.GENERAL_INFORMATION;
        String specialty = "General Medicine";

        if (containsAny(text, "fever", "حرارة", "حمى") && containsAny(text, "days", "ايام", "أيام")) {
            careLevel = CareLevel.URGENT_SAME_DAY;
        } else if (containsAny(text, "rash", "طفح", "skin", "جلد")) {
            specialty = "Dermatology";
            careLevel = CareLevel.ROUTINE_APPOINTMENT;
        } else if (containsAny(text, "tooth", "سن", "أسنان", "اسنان")) {
            specialty = "Dentistry";
            careLevel = CareLevel.ROUTINE_APPOINTMENT;
        } else if (containsAny(text, "child", "baby", "طفل", "رضيع")) {
            specialty = "Pediatrics";
            careLevel = CareLevel.ROUTINE_APPOINTMENT;
        } else if (containsAny(text, "anxious", "anxiety", "قلق", "اكتئاب")) {
            specialty = "Psychiatry";
            careLevel = CareLevel.ROUTINE_APPOINTMENT;
        }

        List<String> followUps = new ArrayList<>();
        List<String> summary = new ArrayList<>();
        String message;
        if (arabic) {
            followUps.add("منذ متى بدأت الأعراض؟");
            followUps.add("هل توجد أعراض أخرى مصاحبة؟");
            summary.add("وصف المريض أعراضاً عامة تحتاج إلى تقييم طبي.");
            message = "هذه معلومات عامة للمساعدة في توجيه الرعاية وليست تشخيصاً. "
                    + "يرجى مراجعة الطبيب المختص لتقييم حالتك.";
        } else {
            followUps.add("When did your symptoms start?");
            followUps.add("Are there any other symptoms you have noticed?");
            summary.add("The patient described general symptoms that warrant clinical assessment.");
            message = "This is general information to help you navigate care — it is not a diagnosis. "
                    + "Please see a qualified clinician to have your situation assessed.";
        }

        return new ModelResponse(careLevel, specialty, followUps, summary, message);
    }

    private boolean containsAny(String haystack, String... needles) {
        for (String n : needles) {
            if (haystack.contains(n)) {
                return true;
            }
        }
        return false;
    }
}
