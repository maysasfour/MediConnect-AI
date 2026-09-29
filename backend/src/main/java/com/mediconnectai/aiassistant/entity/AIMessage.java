package com.mediconnectai.aiassistant.entity;
import java.time.Instant;
public record AIMessage(String id, String role, String content, Instant createdAt) {}
