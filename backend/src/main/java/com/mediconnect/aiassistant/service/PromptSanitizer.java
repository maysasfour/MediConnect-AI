package com.mediconnect.aiassistant.service;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Input/output guardrails for the AI assistant.
 *
 * <p>Two jobs:
 * <ul>
 *   <li><b>Input</b>: neutralize common prompt-injection attempts before user
 *       text reaches the model, and cap length. We do not silently trust or
 *       execute instructions embedded in symptom text.</li>
 *   <li><b>Output</b>: reject model output that strays into diagnosis or
 *       prescribing, which the assistant is contractually forbidden from doing.</li>
 * </ul>
 */
@Component
public class PromptSanitizer {

    private static final int MAX_INPUT_CHARS = 2000;

    private static final List<Pattern> INJECTION_PATTERNS = List.of(
            Pattern.compile("(?i)ignore (all |the )?(previous|prior|above) (instructions|prompts)"),
            Pattern.compile("(?i)disregard (all |the )?(previous|prior|system)"),
            Pattern.compile("(?i)you are now (a|an|the)"),
            Pattern.compile("(?i)system prompt"),
            Pattern.compile("(?i)reveal (your|the) (prompt|instructions|system)"),
            Pattern.compile("(?i)act as (a|an) (?!patient)"),
            Pattern.compile("(?i)developer mode"),
            Pattern.compile("(?i)pretend (to be|you are)"));

    // Output phrases that indicate the model is diagnosing or prescribing.
    private static final List<Pattern> UNSAFE_OUTPUT_PATTERNS = List.of(
            Pattern.compile("(?i)you (have|are suffering from|are diagnosed with)\\b"),
            Pattern.compile("(?i)your diagnosis is"),
            Pattern.compile("(?i)i diagnose"),
            Pattern.compile("(?i)take \\d+\\s*(mg|milligrams|tablets?|pills?)"),
            Pattern.compile("(?i)(prescrib|i recommend taking) "),
            Pattern.compile("(?i)\\b(dose|dosage) (of|is)\\b"));

    public record SanitizedInput(String text, boolean injectionDetected) {
    }

    public SanitizedInput sanitizeInput(String rawInput) {
        String stripped = rawInput == null ? "" : rawInput.strip();
        final String text = stripped.length() > MAX_INPUT_CHARS
                ? stripped.substring(0, MAX_INPUT_CHARS)
                : stripped;
        boolean injection = INJECTION_PATTERNS.stream().anyMatch(p -> p.matcher(text).find());
        // Wrap user content so the model treats it strictly as data, not instructions.
        String wrapped = "<<<USER_SYMPTOM_TEXT>>>\n" + text + "\n<<<END_USER_SYMPTOM_TEXT>>>";
        return new SanitizedInput(wrapped, injection);
    }

    /**
     * @return true if the model output is safe to show; false if it appears to
     * diagnose or prescribe and must be replaced with a safe fallback.
     */
    public boolean isOutputSafe(String modelOutput) {
        if (modelOutput == null || modelOutput.isBlank()) {
            return false;
        }
        return UNSAFE_OUTPUT_PATTERNS.stream().noneMatch(p -> p.matcher(modelOutput).find());
    }
}
