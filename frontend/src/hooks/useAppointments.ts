import { useEffect, useState } from 'react';
import { getAppointments } from '../services/appointmentService';
import type { Appointment } from '../types/appointment';

export function useAppointments(initialAppointments: Appointment[] = []) {
  const [appointments, setAppointments] = useState<Appointment[]>(initialAppointments);

  useEffect(() => {
    getAppointments().then(setAppointments).catch(() => undefined);
  }, []);

  return { appointments, setAppointments };
}
