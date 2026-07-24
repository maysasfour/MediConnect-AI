package com.mediconnect.scheduling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mediconnect.scheduling.domain.Appointment;
import com.mediconnect.scheduling.domain.AppointmentStatus;
import com.mediconnect.shared.error.DomainExceptions.BusinessRuleException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppointmentStatusTest {

    private Appointment newAppointment() {
        Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        return new Appointment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), start, start.plus(30, ChronoUnit.MINUTES), "key-1");
    }

    @Test
    @DisplayName("Legal transition path REQUESTED -> COMPLETED succeeds")
    void legalPath() {
        Appointment a = newAppointment();
        a.transitionTo(AppointmentStatus.CONFIRMED);
        a.transitionTo(AppointmentStatus.CHECKED_IN);
        a.transitionTo(AppointmentStatus.IN_PROGRESS);
        a.transitionTo(AppointmentStatus.COMPLETED);
        assertThat(a.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Illegal jump REQUESTED -> COMPLETED is rejected")
    void illegalJump() {
        Appointment a = newAppointment();
        assertThatThrownBy(() -> a.transitionTo(AppointmentStatus.COMPLETED))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot move appointment");
    }

    @Test
    @DisplayName("A terminal state cannot be left")
    void terminalStatesAreFinal() {
        Appointment a = newAppointment();
        a.transitionTo(AppointmentStatus.CANCELLED);
        assertThatThrownBy(() -> a.transitionTo(AppointmentStatus.CONFIRMED))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("Only active statuses occupy a slot")
    void slotOccupancy() {
        assertThat(AppointmentStatus.CONFIRMED.occupiesSlot()).isTrue();
        assertThat(AppointmentStatus.IN_PROGRESS.occupiesSlot()).isTrue();
        assertThat(AppointmentStatus.CANCELLED.occupiesSlot()).isFalse();
        assertThat(AppointmentStatus.NO_SHOW.occupiesSlot()).isFalse();
        assertThat(AppointmentStatus.COMPLETED.occupiesSlot()).isFalse();
    }

    @Test
    @DisplayName("End time must be after start time")
    void rejectsInvalidTimeRange() {
        Instant start = Instant.now();
        assertThatThrownBy(() -> new Appointment(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), start, start.minusSeconds(60), "k"))
                .isInstanceOf(BusinessRuleException.class);
    }
}
