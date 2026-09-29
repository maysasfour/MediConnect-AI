package com.mediconnectai.notification.entity;
public record NotificationTemplate(String id, String name, String channel, String subject, String bodyTemplate) {
    public String render(String value) { return bodyTemplate.replace("{{value}}", value == null ? "" : value); }
}
