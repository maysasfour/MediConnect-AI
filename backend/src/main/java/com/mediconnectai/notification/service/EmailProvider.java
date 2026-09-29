package com.mediconnectai.notification.service;
public interface EmailProvider { boolean send(String recipient, String subject, String body); }
