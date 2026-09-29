import { apiRequest } from '../api/axios';
import { endpoints } from '../api/endpoints';
import type { MedicalRecord } from '../types/medicalRecord';

export function getMedicalRecords(patientId: string): Promise<MedicalRecord[]> {
  return apiRequest<MedicalRecord[]>(endpoints.patientRecords(patientId));
}
