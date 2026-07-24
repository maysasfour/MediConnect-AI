package com.mediconnect.shared;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mediconnect.patient.domain.Patient;
import com.mediconnect.shared.error.DomainExceptions.TenantViolationException;
import com.mediconnect.shared.tenant.TenantContext;
import com.mediconnect.shared.tenant.TenantGuard;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TenantGuardTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    private Patient patientOfClinic(UUID clinicId) {
        return new Patient(clinicId, "MRN-1", "Test Patient", LocalDate.of(1990, 1, 1), "F");
    }

    @Test
    @DisplayName("Access to an object in the current tenant is allowed")
    void allowsSameTenant() {
        UUID clinic = UUID.randomUUID();
        TenantContext.set(clinic);
        Patient patient = patientOfClinic(clinic);
        assertThatCode(() -> TenantGuard.verifyOwnership(patient)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Access to an object in a different tenant is rejected")
    void rejectsCrossTenant() {
        TenantContext.set(UUID.randomUUID());
        Patient otherClinicPatient = patientOfClinic(UUID.randomUUID());
        assertThatThrownBy(() -> TenantGuard.verifyOwnership(otherClinicPatient))
                .isInstanceOf(TenantViolationException.class);
    }

    @Test
    @DisplayName("Requiring a tenant with none bound fails fast")
    void requiresBoundTenant() {
        TenantContext.clear();
        assertThatThrownBy(TenantContext::require).isInstanceOf(IllegalStateException.class);
    }
}
