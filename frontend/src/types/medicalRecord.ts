export type MedicalRecord = {
  id: string;
  patientId: string;
  date: string;
  doctorName: string;
  diagnosis: string;
  notes: string;
  prescriptions: string[];
};
