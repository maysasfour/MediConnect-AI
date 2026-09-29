package com.mediconnectai.appointment.entity;
import java.time.LocalDate;
import java.time.LocalTime;
public record ScheduleException(String id, String doctorName, LocalDate date, LocalTime startsAt, LocalTime endsAt, String reason) {}
