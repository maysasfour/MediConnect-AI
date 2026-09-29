package com.mediconnectai.aiassistant.entity;
import java.time.Instant;
import java.util.List;
public record AIConversation(String id, String patientId, String createdBy, Instant createdAt, List<AIMessage> messages) {
    public AIConversation { messages = messages == null ? List.of() : List.copyOf(messages); }
}
