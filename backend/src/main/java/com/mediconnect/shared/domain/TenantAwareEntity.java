package com.mediconnect.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Base for entities that belong to a single clinic (tenant). The {@code clinicId}
 * discriminator is the backbone of the shared-database multi-tenancy strategy:
 * every query against these entities is filtered by clinic id on the server so
 * one clinic can never read or mutate another clinic's data.
 */
@MappedSuperclass
public abstract class TenantAwareEntity extends BaseEntity {

    @Column(name = "clinic_id", nullable = false, updatable = false)
    private UUID clinicId;

    public UUID getClinicId() {
        return clinicId;
    }

    public void setClinicId(UUID clinicId) {
        this.clinicId = clinicId;
    }
}
