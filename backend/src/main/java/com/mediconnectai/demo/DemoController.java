package com.mediconnectai.demo;

import com.mediconnectai.demo.DemoModels.AiRequest;
import com.mediconnectai.demo.DemoModels.AiResponse;
import com.mediconnectai.demo.DemoModels.Appointment;
import com.mediconnectai.demo.DemoModels.AppointmentRequest;
import com.mediconnectai.demo.DemoModels.AuthResponse;
import com.mediconnectai.demo.DemoModels.DashboardStats;
import com.mediconnectai.demo.DemoModels.LoginRequest;
import com.mediconnectai.demo.DemoModels.MedicalRecord;
import com.mediconnectai.demo.DemoModels.Patient;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {
    private final DemoStore store;

    public DemoController(DemoStore store) {
        this.store = store;
    }

    @GetMapping("/health")
    Map<String, String> health() {
        return Map.of("status", "ok", "service", "MediConnect AI");
    }

    @PostMapping("/auth/login")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return store.findUser(request.email())
            .map(user -> ResponseEntity.ok(new AuthResponse("demo-token-" + user.role().toLowerCase(), user)))
            .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @GetMapping("/patients")
    List<Patient> patients() {
        return store.patients();
    }

    @GetMapping("/patients/{id}")
    ResponseEntity<Patient> patient(@PathVariable String id) {
        return store.patient(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/appointments")
    List<Appointment> appointments() {
        return store.appointments();
    }

    @PostMapping("/appointments")
    Appointment bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        return store.book(request);
    }

    @GetMapping("/patients/{id}/records")
    List<MedicalRecord> medicalRecords(@PathVariable String id) {
        return store.medicalRecords(id);
    }

    @GetMapping("/doctors")
    List<String> doctors() {
        return store.doctors();
    }

    @GetMapping("/analytics/summary")
    DashboardStats dashboardStats() {
        return store.stats();
    }

    @PostMapping("/ai/triage-summary")
    AiResponse aiSummary(@Valid @RequestBody AiRequest request) {
        return new AiResponse(
            "Demo summary: the symptoms should be reviewed by a clinician, with urgency based on breathing difficulty, chest pain, high fever, or sudden worsening.",
            List.of("This is not a diagnosis.", "Seek emergency care for severe or rapidly worsening symptoms."),
            List.of("Capture symptom duration and severity.", "Check relevant medical history.", "Book a clinician follow-up if symptoms persist.")
        );
    }
}
