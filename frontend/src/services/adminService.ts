import { apiRequest } from '../api/axios';
import { endpoints } from '../api/endpoints';
import type { UserProfile } from '../types/user';

type ApiUser = { id: string; fullName: string; email: string; role: string };

export async function getUsers(): Promise<UserProfile[]> {
  const users = await apiRequest<ApiUser[]>(endpoints.users);
  return users.map((user) => ({ id: user.id, name: user.fullName, email: user.email, role: user.role }));
}

export type Clinic = { id: string; name: string; phone: string; email: string; timezone: string };

export function getClinics(): Promise<Clinic[]> {
  return apiRequest<Clinic[]>(endpoints.clinics);
}
