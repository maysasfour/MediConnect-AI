package com.mediconnectai.privacy.service;
import com.mediconnectai.privacy.entity.Consent;
import com.mediconnectai.privacy.entity.PrivacyRequest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
@Service
public class PrivacyService {
    private final List<Consent> consents = new CopyOnWriteArrayList<>();
    private final List<PrivacyRequest> requests = new CopyOnWriteArrayList<>();
    public Consent setConsent(String patientId, String purpose, boolean granted) {
        Consent consent = new Consent(UUID.randomUUID().toString(), patientId, purpose, granted, Instant.now()); consents.add(consent); return consent;
    }
    public PrivacyRequest requestExport(String patientId) {
        PrivacyRequest request = new PrivacyRequest(UUID.randomUUID().toString(), patientId, "Data export", "Pending", Instant.now(), null); requests.add(request); return request;
    }
    public List<Consent> consentsFor(String patientId) { return consents.stream().filter(item -> item.patientId().equals(patientId)).toList(); }
}
