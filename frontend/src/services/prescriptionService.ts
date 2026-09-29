import { apiRequest } from '../api/axios';
import { endpoints } from '../api/endpoints';
import type { Prescription, PrescriptionItem } from '../types/prescription';

export function getPrescriptions(patientId: string): Promise<Prescription[]> {
  return apiRequest<Prescription[]>(endpoints.patientPrescriptions(patientId));
}

export function createPrescription(payload: {
  patientId: string;
  doctorName: string;
  items: PrescriptionItem[];
}): Promise<Prescription> {
  return apiRequest<Prescription>(endpoints.prescriptions, {
    method: 'POST',
    body: JSON.stringify({ ...payload, prescribedOn: new Date().toISOString().slice(0, 10), status: 'Active' })
  });
}
