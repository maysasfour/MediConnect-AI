export type UserProfile = {
  id: string;
  name: string;
  email: string;
  role: 'ADMIN' | 'DOCTOR' | 'PATIENT' | string;
};

export type UserRole = 'ADMIN' | 'DOCTOR' | 'PATIENT';
