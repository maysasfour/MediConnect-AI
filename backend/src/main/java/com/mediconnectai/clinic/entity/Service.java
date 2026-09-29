package com.mediconnectai.clinic.entity;

import java.math.BigDecimal;
import java.time.Duration;

public record Service(String id, String departmentId, String name, Duration duration, BigDecimal price) {
}
