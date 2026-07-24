package com.mediconnect.scheduling.domain;

import java.util.Map;
import java.util.Set;

/**
 * Appointment lifecycle states and the legal transitions between them. Modelling
 * transitions explicitly prevents illegal jumps (e.g. reviving a cancelled
 * appointment) from anywhere in the codebase.
 */
public enum AppointmentStatus {
    REQUESTED,
    CONFIRMED,
    CHECKED_IN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_SHOW,
    RESCHEDULED;

    private static final Map<AppointmentStatus, Set<AppointmentStatus>> TRANSITIONS = Map.of(
            REQUESTED, Set.of(CONFIRMED, CANCELLED, RESCHEDULED),
            CONFIRMED, Set.of(CHECKED_IN, CANCELLED, NO_SHOW, RESCHEDULED),
            CHECKED_IN, Set.of(IN_PROGRESS, CANCELLED, NO_SHOW),
            IN_PROGRESS, Set.of(COMPLETED, CANCELLED),
            COMPLETED, Set.of(),
            CANCELLED, Set.of(),
            NO_SHOW, Set.of(),
            RESCHEDULED, Set.of());

    public boolean canTransitionTo(AppointmentStatus target) {
        return TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }

    /** States that occupy a doctor's time slot and therefore block double booking. */
    public boolean occupiesSlot() {
        return this == REQUESTED || this == CONFIRMED || this == CHECKED_IN || this == IN_PROGRESS;
    }
}
