package com.mediconnect.shared.tenant;

import java.util.Optional;
import java.util.UUID;

/**
 * Holds the current request's tenant (clinic) identifier for the duration of a
 * request thread. Populated by {@code TenantFilter} from the authenticated
 * principal and cleared afterwards to avoid leaking state across pooled threads.
 *
 * <p>Every tenant-scoped repository query MUST be constrained by this value on
 * the server; the client can never be trusted to supply its own clinic id.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(UUID clinicId) {
        CURRENT.set(clinicId);
    }

    public static Optional<UUID> current() {
        return Optional.ofNullable(CURRENT.get());
    }

    /**
     * @return the current tenant id, or throws if no tenant is bound. Used by
     * services that must never run outside a tenant scope.
     */
    public static UUID require() {
        UUID id = CURRENT.get();
        if (id == null) {
            throw new IllegalStateException("No tenant bound to the current request");
        }
        return id;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
