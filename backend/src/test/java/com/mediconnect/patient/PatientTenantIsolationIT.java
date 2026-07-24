package com.mediconnect.patient;

import static org.assertj.core.api.Assertions.assertThat;

import com.mediconnect.patient.domain.Patient;
import com.mediconnect.patient.repo.PatientRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Repository integration + tenant-isolation test. Runs Flyway against a real
 * PostgreSQL (so it also validates that the migration matches the JPA mappings)
 * and asserts that clinic-scoped finders never cross a tenant boundary.
 *
 * <p>Skipped automatically when no Docker engine is available; runs in CI.
 */
@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true"
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(PatientTenantIsolationIT.NoAuditing.class)
@ActiveProfiles("test")
class PatientTenantIsolationIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    PatientRepository patients;

    private final UUID clinicA = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID clinicB = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    @DisplayName("A patient is only visible within its own clinic")
    void patientScopedToClinic() {
        Patient a = patients.save(new Patient(clinicA, "MRN-A-1", "Alice A",
                LocalDate.of(1990, 1, 1), "F"));
        patients.save(new Patient(clinicB, "MRN-B-1", "Bob B",
                LocalDate.of(1985, 5, 5), "M"));

        // Correct clinic resolves the patient...
        assertThat(patients.findByIdAndClinicId(a.getId(), clinicA)).isPresent();
        // ...but the other clinic cannot see it, even with the right id.
        assertThat(patients.findByIdAndClinicId(a.getId(), clinicB)).isEmpty();
    }

    @Test
    @DisplayName("Listing is filtered to the current clinic")
    void listingIsClinicScoped() {
        patients.save(new Patient(clinicA, "MRN-A-2", "Carol A",
                LocalDate.of(1970, 3, 3), "F"));
        patients.save(new Patient(clinicB, "MRN-B-2", "Dave B",
                LocalDate.of(1972, 4, 4), "M"));

        var pageA = patients.findByClinicId(clinicA,
                org.springframework.data.domain.PageRequest.of(0, 50));
        assertThat(pageA.getContent()).allMatch(p -> p.getClinicId().equals(clinicA));
    }

    /** Minimal auditing config so @CreatedDate/@LastModifiedDate populate in the slice test. */
    @org.springframework.boot.test.context.TestConfiguration
    @org.springframework.data.jpa.repository.config.EnableJpaAuditing
    static class NoAuditing {
    }
}
