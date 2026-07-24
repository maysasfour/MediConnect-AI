package com.mediconnect.scheduling.domain;

import com.mediconnect.shared.domain.TenantAwareEntity;
import com.mediconnect.shared.error.DomainExceptions.BusinessRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * A booked appointment. Double booking is prevented on two levels: a database
 * unique constraint on {@code (doctor_id, start_time)} for slot-occupying
 * statuses (the hard guarantee under concurrency), and the optimistic-lock
 * {@code version} inherited from {@link com.mediconnect.shared.domain.BaseEntity}.
 * A booking idempotency key makes retried create requests safe.
 */
@Entity
@Table(name = "appointment")
public class Appointment extends TenantAwareEntity {

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.REQUESTED;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @Column(name = "reason")
    private String reason;

    protected Appointment() {
    }

    public Appointment(UUID clinicId, UUID branchId, UUID doctorId, UUID patientId,
                       Instant startTime, Instant endTime, String idempotencyKey) {
        setClinicId(clinicId);
        this.branchId = branchId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        if (!endTime.isAfter(startTime)) {
            throw new BusinessRuleException("INVALID_TIME_RANGE",
                    "Appointment end must be after its start");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.idempotencyKey = idempotencyKey;
    }

    /** Applies a validated status transition, rejecting illegal jumps. */
    public void transitionTo(AppointmentStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new BusinessRuleException("ILLEGAL_STATUS_TRANSITION",
                    "Cannot move appointment from " + status + " to " + target);
        }
        this.status = target;
    }

    public UUID getBranchId() {
        return branchId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
