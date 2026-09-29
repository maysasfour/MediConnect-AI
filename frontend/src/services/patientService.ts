import { apiRequest } from '../api/axios';
import { endpoints } from '../api/endpoints';
import type { Patient } from '../types/patient';

export function getPatients(): Promise<Patient[]> {
  return apiRequest<Patient[]>(endpoints.patients);
}
