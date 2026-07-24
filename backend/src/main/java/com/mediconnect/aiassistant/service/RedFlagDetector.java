package com.mediconnect.aiassistant.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Deterministic, rule-based detection of emergency "red flag" symptoms. This
 * runs <em>before</em> any external AI model is called: if a red flag matches,
 * the normal chat is interrupted and the user is directed to emergency care, and
 * the message is never forwarded to a model provider.
 *
 * <p>Matching is intentionally conservative and bilingual (English + Arabic).
 * Because a false negative here is far more costly than a false positive, the
 * patterns favour recall. This is a safety guardrail, not a diagnostic tool.
 */
@Component
public class RedFlagDetector {

    /** A matched red flag with the phrase that triggered it and a guidance key. */
    public record RedFlag(String category, String matchedTerm) {
    }

    private record Rule(String category, Pattern pattern) {
    }

    // Patterns are matched against normalized, lower-cased text. Arabic terms are
    // included alongside English for the bilingual assistant.
    private static final List<Rule> RULES = List.of(
            rule("CHEST_PAIN",
                    "chest pain", "pressure in (my |the )?chest", "crushing chest",
                    "الم في الصدر", "ألم في الصدر", "الم بالصدر", "ضغط على الصدر"),
            rule("STROKE",
                    "face drooping", "slurred speech", "sudden numbness", "one side",
                    "can'?t move (my )?(arm|leg|face)", "weakness on one side",
                    "شلل", "تنميل مفاجئ", "صعوبة في الكلام", "تدلي الوجه"),
            rule("BREATHING",
                    "can'?t breathe", "cannot breathe", "difficulty breathing",
                    "shortness of breath", "struggling to breathe", "choking",
                    "لا استطيع التنفس", "صعوبة في التنفس", "ضيق (شديد )?في التنفس", "اختناق"),
            rule("SEVERE_BLEEDING",
                    "severe bleeding", "won'?t stop bleeding", "bleeding heavily",
                    "coughing up blood", "vomiting blood",
                    "نزيف حاد", "نزيف شديد", "لا يتوقف النزيف", "تقيؤ دم"),
            rule("SUICIDE_SELF_HARM",
                    "kill myself", "suicid", "end my life", "want to die", "self.?harm",
                    "hurt myself",
                    "انتحار", "اقتل نفسي", "أقتل نفسي", "انهي حياتي", "إيذاء نفسي"),
            rule("ANAPHYLAXIS",
                    "throat.{0,8}clos", "(throat|tongue|face|lips).{0,12}swell",
                    "swell.{0,12}(throat|tongue|face|lips)", "anaphyla", "severe allergic",
                    "تورم (الحلق|اللسان|الوجه)", "حساسية شديدة", "صدمة تحسسية"),
            rule("LOSS_OF_CONSCIOUSNESS",
                    "passed out", "unconscious", "fainted", "unresponsive", "seizure",
                    "convuls",
                    "فقدان الوعي", "اغماء", "إغماء", "تشنج", "نوبة صرع"),
            rule("PREGNANCY_EMERGENCY",
                    "heavy vaginal bleeding", "no fetal movement", "water broke.*bleeding",
                    "نزيف اثناء الحمل", "نزيف أثناء الحمل"),
            rule("STROKE_SUDDEN_HEADACHE",
                    "worst headache of my life", "sudden severe headache", "thunderclap headache",
                    "اسوأ صداع", "أسوأ صداع", "صداع مفاجئ شديد"));

    private static Rule rule(String category, String... terms) {
        String joined = String.join("|", terms);
        return new Rule(category, Pattern.compile("(?i)(" + joined + ")",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE));
    }

    /**
     * @param rawInput free-text user message in Arabic or English
     * @return the first matching red flag, if any
     */
    public Optional<RedFlag> detect(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return Optional.empty();
        }
        String normalized = normalize(rawInput);
        for (Rule rule : RULES) {
            var matcher = rule.pattern().matcher(normalized);
            if (matcher.find()) {
                return Optional.of(new RedFlag(rule.category(), matcher.group(1)));
            }
        }
        return Optional.empty();
    }

    public boolean isEmergency(String rawInput) {
        return detect(rawInput).isPresent();
    }

    /**
     * Lower-case and strip Arabic short-vowel diacritics (harakat) so that
     * inconsistent vocalisation does not let a red flag slip through. We do not
     * apply Unicode decomposition here: decomposing the input but not the
     * patterns would split letters like {@code أ}/{@code ئ} and cause mismatches.
     */
    private String normalize(String input) {
        String lower = input.toLowerCase(Locale.ROOT);
        // U+064B–U+0652 are the harakat (tanwin, short vowels, sukoon, shadda);
        // U+0670 is the superscript alef. None are part of a base letter.
        return lower.replaceAll("[\\u064B-\\u0652\\u0670]", "");
    }
}
