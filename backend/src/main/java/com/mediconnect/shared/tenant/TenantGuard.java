package com.mediconnect.shared.tenant;

import com.mediconnect.shared.domain.TenantAwareEntity;
import com.mediconnect.shared.error.DomainExceptions.TenantViolationException;
import java.util.UUID;

/**
 * Central helper for verifying object ownership against the current tenant. Every
 * service that loads a tenant-scoped entity by id must pass it through
 * {@link #verifyOwnership} so that an id belonging to another clinic can never be
 * read or mutated — the object is reported as "not found" rather than disclosed.
 */
public final class TenantGuard {

    private TenantGuard() {
    }

    public static void verifyOwnership(TenantAwareEntity entity) {
        UUID current = TenantContext.require();
        if (!current.equals(entity.getClinicId())) {
            throw new TenantViolationException(
                    "Object " + entity.getId() + " does not belong to the current tenant");
        }
    }

    public static void verifyOwnership(UUID entityClinicId) {
        UUID current = TenantContext.require();
        if (!current.equals(entityClinicId)) {
            throw new TenantViolationException("Object does not belong to the current tenant");
        }
    }
}
