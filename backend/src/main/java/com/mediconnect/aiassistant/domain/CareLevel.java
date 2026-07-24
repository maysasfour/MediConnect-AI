package com.mediconnect.aiassistant.domain;

/**
 * Care-navigation recommendation produced by the assistant. This is triage
 * guidance only — it is never a diagnosis and never a treatment decision.
 */
public enum CareLevel {
    /** Potentially life-threatening; the chat is interrupted with emergency guidance. */
    EMERGENCY,
    /** Should be seen the same day. */
    URGENT_SAME_DAY,
    /** A routine appointment is appropriate. */
    ROUTINE_APPOINTMENT,
    /** General health information; no clinical concern identified. */
    GENERAL_INFORMATION
}
