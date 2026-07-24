package com.mediconnect.identity.domain;

/**
 * System roles. Authorization is always enforced server-side; the client's
 * claimed role is never trusted. {@code SYSTEM_ADMIN} is the only role that may
 * operate outside a single clinic tenant.
 */
public enum Role {
    PATIENT,
    DOCTOR,
    NURSE,
    RECEPTIONIST,
    CLINIC_ADMIN,
    SYSTEM_ADMIN,
    DATA_PROTECTION_OFFICER;

    /** Spring Security authority name, e.g. {@code ROLE_DOCTOR}. */
    public String authority() {
        return "ROLE_" + name();
    }
}
