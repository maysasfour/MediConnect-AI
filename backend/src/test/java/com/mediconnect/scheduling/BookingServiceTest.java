package com.mediconnect.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mediconnect.scheduling.domain.Appointment;
import com.mediconnect.scheduling.repo.AppointmentRepository;
import com.mediconnect.scheduling.repo.AppointmentStatusHistoryRepository;
import com.mediconnect.scheduling.service.BookingService;
import com.mediconnect.shared.error.DomainExceptions.ConflictException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    AppointmentRepository appointments;
    @Mock
    AppointmentStatusHistoryRepository history;

    private BookingService service() {
        return new BookingService(appointments, history);
    }

    private final UUID clinic = UUID.randomUUID();
    private final UUID branch = UUID.randomUUID();
    private final UUID doctor = UUID.randomUUID();
    private final UUID patient = UUID.randomUUID();
    private final Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
    private final Instant end = start.plus(30, ChronoUnit.MINUTES);

    @Test
    @DisplayName("Booking an open slot succeeds and records history")
    void bookOpenSlot() {
        when(appointments.findByIdempotencyKey("k1")).thenReturn(Optional.empty());
        when(appointments.existsOverlapping(eq(clinic), eq(doctor), any(), any(), anyList()))
                .thenReturn(false);

        Appointment a = service().book(clinic, branch, doctor, patient, start, end, "k1");

        assertThat(a).isNotNull();
        verify(appointments).saveAndFlush(any(Appointment.class));
        verify(history).save(any());
    }

    @Test
    @DisplayName("Overlapping slot is rejected before saving")
    void rejectsOverlap() {
        when(appointments.findByIdempotencyKey("k2")).thenReturn(Optional.empty());
        when(appointments.existsOverlapping(eq(clinic), eq(doctor), any(), any(), anyList()))
                .thenReturn(true);

        assertThatThrownBy(() -> service().book(clinic, branch, doctor, patient, start, end, "k2"))
                .isInstanceOf(ConflictException.class);
        verify(appointments, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Idempotent replay returns the existing appointment without re-booking")
    void idempotentReplay() {
        Appointment existing = new Appointment(clinic, branch, doctor, patient, start, end, "k3");
        when(appointments.findByIdempotencyKey("k3")).thenReturn(Optional.of(existing));

        Appointment result = service().book(clinic, branch, doctor, patient, start, end, "k3");

        assertThat(result).isSameAs(existing);
        verify(appointments, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("A race lost at the DB unique constraint surfaces as a conflict")
    void dbConstraintRaceBecomesConflict() {
        when(appointments.findByIdempotencyKey("k4")).thenReturn(Optional.empty());
        when(appointments.existsOverlapping(eq(clinic), eq(doctor), any(), any(), anyList()))
                .thenReturn(false);
        when(appointments.saveAndFlush(any()))
                .thenThrow(new DataIntegrityViolationException("unique violation"));

        assertThatThrownBy(() -> service().book(clinic, branch, doctor, patient, start, end, "k4"))
                .isInstanceOf(ConflictException.class);
    }
}
