package com.mediconnect.scheduling.domain;

import com.mediconnect.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

/** Append-only record of each appointment status change, for audit and analytics. */
@Entity
@Table(name = "appointment_status_history")
public class AppointmentStatusHistory extends BaseEntity {

    @Column(name = "appointment_id", nullable = false, updatable = false)
    private UUID appointmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", updatable = false)
    private AppointmentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, updatable = false)
    private AppointmentStatus toStatus;

    @Column(name = "changed_by", updatable = false)
    private UUID changedBy;

    protected AppointmentStatusHistory() {
    }

    public AppointmentStatusHistory(UUID appointmentId, AppointmentStatus fromStatus,
                                    AppointmentStatus toStatus, UUID changedBy) {
        this.appointmentId = appointmentId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public AppointmentStatus getFromStatus() {
        return fromStatus;
    }

    public AppointmentStatus getToStatus() {
        return toStatus;
    }

    public UUID getChangedBy() {
        return changedBy;
    }
}
