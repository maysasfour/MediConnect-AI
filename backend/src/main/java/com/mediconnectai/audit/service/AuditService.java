package com.mediconnectai.audit.service;
import com.mediconnectai.audit.entity.AuditLog;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
@Service
public class AuditService {
    private final List<AuditLog> events = new CopyOnWriteArrayList<>();
    public AuditLog record(String actor, String action, String resource, Map<String, String> details) {
        AuditLog event = new AuditLog(UUID.randomUUID().toString(), actor, action, resource, Instant.now(), details); events.add(event); return event;
    }
    public List<AuditLog> recent() { return events.reversed().stream().limit(100).toList(); }
}
