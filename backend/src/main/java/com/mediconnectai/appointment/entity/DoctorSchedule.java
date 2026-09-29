package com.mediconnectai.appointment.entity;
import java.time.DayOfWeek;
import java.time.LocalTime;
public record DoctorSchedule(String id, String doctorName, DayOfWeek day, LocalTime startsAt, LocalTime endsAt) {
    public boolean contains(LocalTime time) { return !time.isBefore(startsAt) && time.isBefore(endsAt); }
}
