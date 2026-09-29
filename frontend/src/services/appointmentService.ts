import { apiRequest } from '../api/axios';
import { endpoints } from '../api/endpoints';
import type { Appointment } from '../types/appointment';

export type AppointmentPayload = {
  patientId: string;
  doctorName: string;
  startsAt: string;
  type: string;
};

export function getAppointments(): Promise<Appointment[]> {
  return apiRequest<Appointment[]>(endpoints.appointments);
}

export function bookAppointment(payload: AppointmentPayload): Promise<Appointment> {
  return apiRequest<Appointment>(endpoints.appointments, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}
