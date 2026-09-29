export const endpoints = {
  login: '/api/auth/login',
  versionedLogin: '/api/v1/auth/login',
  register: '/api/v1/auth/register',
  users: '/api/v1/users',
  patients: '/api/patients',
  appointments: '/api/appointments',
  doctors: '/api/doctors',
  analytics: '/api/analytics/summary',
  aiSummary: '/api/ai/triage-summary',
  patientRecords: (patientId: string) => `/api/patients/${patientId}/records`,
  prescriptions: '/api/v1/prescriptions',
  patientPrescriptions: (patientId: string) => `/api/v1/prescriptions/patient/${patientId}`,
  clinics: '/api/v1/clinics'
};
