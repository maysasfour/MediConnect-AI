import { useEffect, useState } from 'react';
import { getPatients } from '../services/patientService';
import type { Patient } from '../types/patient';

export function usePatient(initialPatients: Patient[] = []) {
  const [patients, setPatients] = useState<Patient[]>(initialPatients);
  const [selectedPatientId, setSelectedPatientId] = useState(initialPatients[0]?.id ?? '');

  useEffect(() => {
    getPatients().then((patientList) => {
      setPatients(patientList);
      setSelectedPatientId(patientList[0]?.id ?? '');
    }).catch(() => undefined);
  }, []);

  return { patients, selectedPatientId, setSelectedPatientId };
}
