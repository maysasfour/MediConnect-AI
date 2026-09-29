package com.mediconnectai.medicalrecord.entity;

import java.time.Instant;

public record VitalSign(String id, String encounterId, String type, double value, String unit,
                        Instant measuredAt) {
}
