package com.mediconnectai.notification.entity;
import java.time.Instant;
public record Notification(String id, String recipient, String channel, String subject, String body, String status, Instant sentAt) {}
