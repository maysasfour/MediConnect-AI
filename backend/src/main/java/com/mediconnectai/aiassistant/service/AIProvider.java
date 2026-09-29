package com.mediconnectai.aiassistant.service;

import com.mediconnectai.aiassistant.entity.AISymptomSummary;

@FunctionalInterface
public interface AIProvider {
    AISymptomSummary summarize(String patientId, String symptoms);
}
