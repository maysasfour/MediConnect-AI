package com.mediconnect.scheduling.repo;

import com.mediconnect.scheduling.domain.AppointmentStatusHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentStatusHistoryRepository
        extends JpaRepository<AppointmentStatusHistory, UUID> {

    List<AppointmentStatusHistory> findByAppointmentIdOrderByCreatedAtAsc(UUID appointmentId);
}
