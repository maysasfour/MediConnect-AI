import { createContext, type ReactNode, useContext, useEffect, useMemo, useState } from 'react';
import { getAiSummary, type AiSummary } from '../services/aiService';
import { getUsers } from '../services/adminService';
import { bookAppointment, getAppointments, type AppointmentPayload } from '../services/appointmentService';
import { login, register } from '../services/authService';
import { getMedicalRecords } from '../services/medicalRecordService';
import { getPatients } from '../services/patientService';
import { createPrescription, getPrescriptions } from '../services/prescriptionService';
import type { Appointment } from '../types/appointment';
import type { AuditEvent } from '../types/audit';
import type { MedicalRecord } from '../types/medicalRecord';
import type { Patient } from '../types/patient';
import type { Prescription, PrescriptionItem } from '../types/prescription';
import type { UserProfile } from '../types/user';

const fallbackPatients: Patient[] = [
  { id: 'p-1001', name: 'Omar Nasser', age: 34, gender: 'Male', phone: '+962790000101', riskLevel: 'Medium', conditions: ['Asthma'], allergies: ['Penicillin'] },
  { id: 'p-1002', name: 'Maya Saleh', age: 42, gender: 'Female', phone: '+962790000102', riskLevel: 'High', conditions: ['Hypertension', 'Type 2 Diabetes'], allergies: ['None reported'] },
  { id: 'p-1003', name: 'Yousef Karim', age: 28, gender: 'Male', phone: '+962790000103', riskLevel: 'Low', conditions: ['Seasonal allergies'], allergies: ['Ibuprofen'] }
];

const fallbackAppointments: Appointment[] = [
  { id: 'a-501', patientId: 'p-1002', patientName: 'Maya Saleh', doctorName: 'Dr. Lina Haddad', startsAt: new Date().toISOString(), type: 'Follow-up', status: 'Confirmed' },
  { id: 'a-502', patientId: 'p-1001', patientName: 'Omar Nasser', doctorName: 'Dr. Samer Khoury', startsAt: new Date(Date.now() + 86400000).toISOString(), type: 'Respiratory consult', status: 'Pending' }
];

const fallbackRecords: MedicalRecord[] = [
  { id: 'r-701', patientId: 'p-1002', date: new Date(Date.now() - 86400000 * 12).toISOString().slice(0, 10), doctorName: 'Dr. Lina Haddad', diagnosis: 'Hypertension review', notes: 'Blood pressure trending down after medication adjustment.', prescriptions: ['Amlodipine 5mg daily'] },
  { id: 'r-702', patientId: 'p-1001', date: new Date(Date.now() - 86400000 * 25).toISOString().slice(0, 10), doctorName: 'Dr. Samer Khoury', diagnosis: 'Asthma maintenance', notes: 'No acute distress. Reviewed inhaler technique.', prescriptions: ['Salbutamol inhaler as needed'] }
];

const demoUsers: Record<string, UserProfile> = {
  'doctor@mediconnect.ai': { id: 'doctor-demo', name: 'Dr. Lina Haddad', email: 'doctor@mediconnect.ai', role: 'DOCTOR' },
  'patient@mediconnect.ai': { id: 'p-1001', name: 'Omar Nasser', email: 'patient@mediconnect.ai', role: 'PATIENT' },
  'admin@mediconnect.ai': { id: 'admin-demo', name: 'Maya Admin', email: 'admin@mediconnect.ai', role: 'ADMIN' }
};

type AppContextValue = {
  user: UserProfile | null;
  patients: Patient[];
  appointments: Appointment[];
  records: MedicalRecord[];
  prescriptions: Prescription[];
  users: UserProfile[];
  selectedPatient: Patient;
  selectedPatientId: string;
  apiStatus: 'Connected' | 'Offline demo';
  loading: boolean;
  toast: string;
  auditEvents: AuditEvent[];
  signIn: (email: string, password: string) => Promise<void>;
  signUp: (name: string, email: string, password: string) => Promise<void>;
  signOut: () => void;
  selectPatient: (patientId: string) => void;
  bookVisit: (payload: AppointmentPayload) => Promise<void>;
  prescribe: (patientId: string, items: PrescriptionItem[]) => Promise<void>;
  summarizeSymptoms: (message: string, patientId?: string) => Promise<AiSummary>;
  notify: (message: string) => void;
};

const AppContext = createContext<AppContextValue | null>(null);

export function AppProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(() => {
    const saved = localStorage.getItem('mediconnect-user');
    return saved ? JSON.parse(saved) as UserProfile : null;
  });
  const [patients, setPatients] = useState(fallbackPatients);
  const [appointments, setAppointments] = useState(fallbackAppointments);
  const [records, setRecords] = useState(fallbackRecords);
  const [prescriptions, setPrescriptions] = useState<Prescription[]>([]);
  const [users, setUsers] = useState<UserProfile[]>([]);
  const [selectedPatientId, setSelectedPatientId] = useState('p-1002');
  const [apiStatus, setApiStatus] = useState<'Connected' | 'Offline demo'>('Offline demo');
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState('');
  const [auditEvents, setAuditEvents] = useState<AuditEvent[]>([
    { id: 'audit-1', actor: 'System', action: 'Demo workspace initialized', resource: 'Application', occurredAt: new Date().toISOString() }
  ]);

  const selectedPatient = patients.find((patient) => patient.id === selectedPatientId) ?? patients[0];

  useEffect(() => {
    if (!user) return;
    setLoading(true);
    Promise.allSettled([getPatients(), getAppointments(), getUsers()]).then(([patientResult, appointmentResult, userResult]) => {
      if (patientResult.status === 'fulfilled') {
        setPatients(patientResult.value);
        setSelectedPatientId(user.role === 'PATIENT' ? 'p-1001' : patientResult.value[0]?.id ?? 'p-1002');
        setApiStatus('Connected');
      }
      if (appointmentResult.status === 'fulfilled') setAppointments(appointmentResult.value);
      if (userResult.status === 'fulfilled') setUsers(userResult.value);
      setLoading(false);
    });
  }, [user]);

  useEffect(() => {
    if (!selectedPatientId) return;
    getMedicalRecords(selectedPatientId).then(setRecords).catch(() => {
      setRecords(fallbackRecords.filter((record) => record.patientId === selectedPatientId));
    });
    getPrescriptions(selectedPatientId).then(setPrescriptions).catch(() => setPrescriptions([]));
  }, [selectedPatientId]);

  function addAudit(action: string, resource: string) {
    setAuditEvents((current) => [{ id: crypto.randomUUID(), actor: user?.name ?? 'Guest', action, resource, occurredAt: new Date().toISOString() }, ...current]);
  }

  function notify(message: string) {
    setToast(message);
    window.setTimeout(() => setToast(''), 3500);
  }

  async function signIn(email: string, password: string) {
    setLoading(true);
    try {
      let response;
      try {
        response = await login(email, password);
        setApiStatus('Connected');
      } catch (error) {
        const demoUser = demoUsers[email.toLowerCase()];
        const expectedPassword = `${email.split('@')[0]}123`;
        if (!demoUser || password !== expectedPassword) throw error;
        response = { token: 'offline-demo-token', user: demoUser };
        setApiStatus('Offline demo');
      }
      setUser(response.user);
      localStorage.setItem('mediconnect-user', JSON.stringify(response.user));
      localStorage.setItem('mediconnect-token', response.token);
      notify(`Welcome back, ${response.user.name}`);
    } finally {
      setLoading(false);
    }
  }

  async function signUp(name: string, email: string, password: string) {
    setLoading(true);
    try {
      let response;
      try {
        response = await register(name, email, password);
        setApiStatus('Connected');
      } catch {
        response = { token: 'offline-demo-token', user: { id: `local-${Date.now()}`, name, email, role: 'PATIENT' as const } };
        setApiStatus('Offline demo');
      }
      setUser(response.user);
      localStorage.setItem('mediconnect-user', JSON.stringify(response.user));
      localStorage.setItem('mediconnect-token', response.token);
      notify('Your patient account is ready.');
    } finally {
      setLoading(false);
    }
  }

  function signOut() {
    setUser(null);
    localStorage.removeItem('mediconnect-user');
    localStorage.removeItem('mediconnect-token');
    window.location.hash = '#/login';
  }

  async function bookVisit(payload: AppointmentPayload) {
    try {
      const appointment = await bookAppointment(payload);
      setAppointments((current) => [...current, appointment]);
      setApiStatus('Connected');
    } catch {
      const patient = patients.find((item) => item.id === payload.patientId);
      setAppointments((current) => [...current, { id: `local-${Date.now()}`, patientId: payload.patientId, patientName: patient?.name ?? 'Patient', doctorName: payload.doctorName, startsAt: payload.startsAt, type: payload.type, status: 'Pending' }]);
      setApiStatus('Offline demo');
    }
    addAudit('Booked appointment', payload.patientId);
    notify('Appointment request submitted.');
  }

  async function prescribe(patientId: string, items: PrescriptionItem[]) {
    let prescription: Prescription;
    try {
      prescription = await createPrescription({ patientId, doctorName: user?.name ?? 'Clinician', items });
      setApiStatus('Connected');
    } catch {
      prescription = { id: `local-${Date.now()}`, patientId, doctorName: user?.name ?? 'Clinician', prescribedOn: new Date().toISOString().slice(0, 10), status: 'Active', items };
      setApiStatus('Offline demo');
    }
    setPrescriptions((current) => [prescription, ...current]);
    addAudit('Created prescription', patientId);
    notify('Prescription saved successfully.');
  }

  async function summarizeSymptoms(message: string, patientId?: string) {
    let summary: AiSummary;
    try {
      summary = await getAiSummary(message, patientId);
      setApiStatus('Connected');
    } catch {
      summary = {
        summary: `You reported: ${message.trim()} This demo summary can help you organize the details for a healthcare professional.`,
        safetyNotes: ['This information is educational and is not a diagnosis.', 'Seek urgent care for severe, sudden, or rapidly worsening symptoms.'],
        suggestedNextSteps: ['Note when the symptoms began and what changes them.', 'Contact a qualified healthcare professional if symptoms persist or concern you.']
      };
      setApiStatus('Offline demo');
    }
    addAudit('Generated AI symptom summary', patientId ?? 'Self assessment');
    return summary;
  }

  const value = useMemo<AppContextValue>(() => ({
    user, patients, appointments, records, prescriptions, users, selectedPatient, selectedPatientId,
    apiStatus, loading, toast, auditEvents, signIn, signUp, signOut, selectPatient: setSelectedPatientId,
    bookVisit, prescribe, summarizeSymptoms, notify
  }), [user, patients, appointments, records, prescriptions, users, selectedPatient, selectedPatientId, apiStatus, loading, toast, auditEvents]);

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) throw new Error('useApp must be used inside AppProvider');
  return context;
}
