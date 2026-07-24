package com.mediconnect.aiassistant;

import static org.assertj.core.api.Assertions.assertThat;

import com.mediconnect.aiassistant.service.RedFlagDetector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RedFlagDetectorTest {

    private final RedFlagDetector detector = new RedFlagDetector();

    @ParameterizedTest
    @DisplayName("English emergency phrases are flagged")
    @ValueSource(strings = {
            "I have severe chest pain radiating to my arm",
            "I can't breathe properly since this morning",
            "my father is having slurred speech and face drooping",
            "the wound won't stop bleeding",
            "I want to kill myself",
            "my throat is closing and my tongue is swelling",
            "she passed out and is unresponsive",
            "this is the worst headache of my life"
    })
    void detectsEnglishRedFlags(String input) {
        assertThat(detector.isEmergency(input)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("Arabic emergency phrases are flagged")
    @ValueSource(strings = {
            "عندي ألم في الصدر شديد",
            "لا استطيع التنفس منذ ساعة",
            "حصل عندي تنميل مفاجئ في اليد",
            "يوجد نزيف حاد لا يتوقف",
            "افكر في الانتحار",
            "فقدان الوعي والتشنج"
    })
    void detectsArabicRedFlags(String input) {
        assertThat(detector.isEmergency(input)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("Non-emergency messages are not flagged")
    @ValueSource(strings = {
            "I have a mild sore throat for two days",
            "عندي رشح بسيط منذ يومين",
            "I would like to book a routine dental checkup",
            "my child has a small rash on the arm"
    })
    void doesNotFlagRoutineMessages(String input) {
        assertThat(detector.isEmergency(input)).isFalse();
    }

    @Test
    @DisplayName("Empty or null input is never an emergency")
    void handlesEmptyInput() {
        assertThat(detector.isEmergency(null)).isFalse();
        assertThat(detector.isEmergency("   ")).isFalse();
    }

    @Test
    @DisplayName("Detection is case-insensitive and ignores Arabic diacritics")
    void normalizesInput() {
        assertThat(detector.isEmergency("SEVERE CHEST PAIN")).isTrue();
        assertThat(detector.detect("عندي أَلَم فِي الصَّدْر").isPresent()).isTrue();
    }
}
