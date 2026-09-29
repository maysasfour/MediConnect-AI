export type PrescriptionItem = {
  id?: string;
  medicationName: string;
  dosage: string;
  frequency: string;
  durationDays: number;
  instructions: string;
};

export type Prescription = {
  id: string;
  patientId: string;
  doctorName: string;
  prescribedOn: string;
  status: string;
  items: PrescriptionItem[];
};
