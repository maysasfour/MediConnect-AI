package com.mediconnect.aiassistant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mediconnect.aiassistant.domain.CareLevel;
import com.mediconnect.aiassistant.provider.AiModelProvider;
import com.mediconnect.aiassistant.provider.AiModelProvider.ModelResponse;
import com.mediconnect.aiassistant.service.PromptSanitizer;
import com.mediconnect.aiassistant.service.RedFlagDetector;
import com.mediconnect.aiassistant.service.SymptomTriageService;
import com.mediconnect.aiassistant.service.SymptomTriageService.TriageResult;
import com.mediconnect.shared.error.DomainExceptions.ForbiddenOperationException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymptomTriageServiceTest {

    @Mock
    AiModelProvider provider;

    // Real detector and sanitizer — the safety rules under test are theirs.
    RedFlagDetector detector = new RedFlagDetector();
    PromptSanitizer sanitizer = new PromptSanitizer();

    private SymptomTriageService service() {
        return new SymptomTriageService(detector, sanitizer, provider);
    }

    @Test
    @DisplayName("Consent is required before any processing")
    void requiresConsent() {
        assertThatThrownBy(() -> service().assess(false, "I have a headache", "en", List.of()))
                .isInstanceOf(ForbiddenOperationException.class);
        verify(provider, never()).analyzeSymptoms(any());
    }

    @Test
    @DisplayName("A red flag interrupts the chat and the model is never called")
    void redFlagBypassesModel() {
        TriageResult result = service().assess(true,
                "I have severe chest pain and can't breathe", "en", List.of());

        assertThat(result.emergency()).isTrue();
        assertThat(result.careLevel()).isEqualTo(CareLevel.EMERGENCY);
        assertThat(result.assistantMessage()).containsIgnoringCase("emergency");
        verify(provider, never()).analyzeSymptoms(any());
    }

    @Test
    @DisplayName("Arabic red flag also interrupts and bypasses the model")
    void arabicRedFlagBypassesModel() {
        TriageResult result = service().assess(true, "عندي نزيف حاد لا يتوقف", "ar", List.of());

        assertThat(result.emergency()).isTrue();
        verify(provider, never()).analyzeSymptoms(any());
    }

    @Test
    @DisplayName("Non-emergency input is forwarded to the provider and returned")
    void nonEmergencyCallsProvider() {
        when(provider.analyzeSymptoms(any())).thenReturn(new ModelResponse(
                CareLevel.ROUTINE_APPOINTMENT, "Dermatology",
                List.of("How long?"), List.of("rash noted"),
                "This is general information, not a diagnosis. Please see a clinician."));

        TriageResult result = service().assess(true,
                "I have a mild rash on my arm", "en", List.of());

        assertThat(result.emergency()).isFalse();
        assertThat(result.careLevel()).isEqualTo(CareLevel.ROUTINE_APPOINTMENT);
        assertThat(result.suggestedSpecialty()).isEqualTo("Dermatology");
        verify(provider).analyzeSymptoms(any());
    }

    @Test
    @DisplayName("Model output that diagnoses or prescribes is replaced with a safe fallback")
    void unsafeOutputReplaced() {
        when(provider.analyzeSymptoms(any())).thenReturn(new ModelResponse(
                CareLevel.GENERAL_INFORMATION, "General Medicine",
                List.of(), List.of(),
                "You have bacterial pneumonia. Take 500 mg amoxicillin twice a day."));

        TriageResult result = service().assess(true,
                "I have a cough", "en", List.of());

        assertThat(result.assistantMessage()).doesNotContainIgnoringCase("amoxicillin");
        assertThat(result.assistantMessage()).containsIgnoringCase("can't provide a diagnosis");
    }
}
