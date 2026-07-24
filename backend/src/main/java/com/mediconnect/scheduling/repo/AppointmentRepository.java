package com.mediconnect.scheduling.repo;

import com.mediconnect.scheduling.domain.Appointment;
import com.mediconnect.scheduling.domain.AppointmentStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Optional<Appointment> findByIdempotencyKey(String idempotencyKey);

    /**
     * Overlap check for slot-occupying appointments for a doctor. Combined with
     * the database unique constraint, this rejects double bookings even under
     * concurrent requests.
     */
    @Query("""
            select case when count(a) > 0 then true else false end
            from Appointment a
            where a.clinicId = :clinicId
              and a.doctorId = :doctorId
              and a.status in :activeStatuses
              and a.startTime < :endTime
              and a.endTime > :startTime
            """)
    boolean existsOverlapping(@Param("clinicId") UUID clinicId,
                              @Param("doctorId") UUID doctorId,
                              @Param("startTime") Instant startTime,
                              @Param("endTime") Instant endTime,
                              @Param("activeStatuses") List<AppointmentStatus> activeStatuses);

    List<Appointment> findByClinicIdAndDoctorIdAndStartTimeBetween(
            UUID clinicId, UUID doctorId, Instant from, Instant to);
}
