package com.mediconnect.scheduling.service;

import com.mediconnect.scheduling.domain.Appointment;
import com.mediconnect.scheduling.domain.AppointmentStatus;
import com.mediconnect.scheduling.domain.AppointmentStatusHistory;
import com.mediconnect.scheduling.repo.AppointmentRepository;
import com.mediconnect.scheduling.repo.AppointmentStatusHistoryRepository;
import com.mediconnect.shared.error.DomainExceptions.ConflictException;
import com.mediconnect.shared.error.DomainExceptions.ResourceNotFoundException;
import com.mediconnect.shared.error.DomainExceptions.TenantViolationException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Appointment booking with double-booking protection and idempotency.
 *
 * <p>Concurrency strategy: an application-level overlap check gives a friendly
 * error in the common case, and a database unique constraint on the doctor slot
 * is the authoritative guard — if two requests race past the check, exactly one
 * commit succeeds and the other surfaces as a {@link ConflictException}. Retried
 * create requests carrying the same idempotency key return the original booking
 * instead of creating a duplicate.
 */
@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private static final List<AppointmentStatus> ACTIVE = List.of(
            AppointmentStatus.REQUESTED, AppointmentStatus.CONFIRMED,
            AppointmentStatus.CHECKED_IN, AppointmentStatus.IN_PROGRESS);

    private final AppointmentRepository appointments;
    private final AppointmentStatusHistoryRepository history;

    public BookingService(AppointmentRepository appointments,
                          AppointmentStatusHistoryRepository history) {
        this.appointments = appointments;
        this.history = history;
    }

    @Transactional
    public Appointment book(UUID clinicId, UUID branchId, UUID doctorId, UUID patientId,
                            Instant startTime, Instant endTime, String idempotencyKey) {
        if (idempotencyKey != null) {
            var existing = appointments.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return existing.get(); // idempotent replay
            }
        }
        if (appointments.existsOverlapping(clinicId, doctorId, startTime, endTime, ACTIVE)) {
            throw new ConflictException("The selected time slot is no longer available");
        }
        Appointment appointment = new Appointment(
                clinicId, branchId, doctorId, patientId, startTime, endTime, idempotencyKey);
        try {
            appointments.saveAndFlush(appointment);
        } catch (DataIntegrityViolationException ex) {
            // Lost the race at the database unique constraint.
            log.info("Double-booking prevented by unique constraint for doctor {}", doctorId);
            throw new ConflictException("The selected time slot is no longer available");
        }
        recordHistory(appointment.getId(), null, AppointmentStatus.REQUESTED, patientId);
        return appointment;
    }

    @Transactional
    public Appointment transition(UUID clinicId, UUID appointmentId,
                                  AppointmentStatus target, UUID actorId) {
        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (!appointment.getClinicId().equals(clinicId)) {
            throw new TenantViolationException("Appointment belongs to another clinic");
        }
        AppointmentStatus from = appointment.getStatus();
        appointment.transitionTo(target);
        appointments.save(appointment);
        recordHistory(appointmentId, from, target, actorId);
        return appointment;
    }

    private void recordHistory(UUID appointmentId, AppointmentStatus from,
                               AppointmentStatus to, UUID actorId) {
        history.save(new AppointmentStatusHistory(appointmentId, from, to, actorId));
    }
}
