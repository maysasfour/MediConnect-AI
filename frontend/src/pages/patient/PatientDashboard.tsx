import { CalendarCheck, FileHeart, HeartPulse, MessageCircleHeart } from 'lucide-react';
import { MetricCard } from '../../components/MetricCard';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function PatientDashboard() {
  const { selectedPatient, appointments, records, prescriptions, user } = useApp();
  const myAppointments = appointments.filter((item) => item.patientId === selectedPatient.id);
  const next = myAppointments.slice().sort((a, b) => new Date(a.startsAt).getTime() - new Date(b.startsAt).getTime())[0];
  return (
    <>
      <PageHeader title={`Welcome, ${user?.name.split(' ')[0] ?? selectedPatient.name.split(' ')[0]}`} description="Your care plan, appointments, and health information in one place." action={<button className="primary-button" type="button" onClick={() => window.location.hash = '#/patient/book'}>Book appointment</button>} />
      <section className="patient-welcome panel"><div><span className="hero-badge light"><HeartPulse size={16} /> Your health overview</span><h2>Small steps create lasting health.</h2><p>Stay connected with your care team and keep your health information up to date.</p></div><div className="wellness-ring"><strong>82</strong><span>Profile<br />complete</span></div></section>
      <section className="stat-grid patient-stats">
        <MetricCard label="Appointments" value={myAppointments.length} detail="Upcoming and recent" icon={<CalendarCheck />} />
        <MetricCard label="Health records" value={records.length} detail="Available documents" icon={<FileHeart />} tone="blue" />
        <MetricCard label="Prescriptions" value={prescriptions.length} detail="Active medications" icon={<HeartPulse />} tone="amber" />
        <MetricCard label="Care support" value="24/7" detail="AI-guided information" icon={<MessageCircleHeart />} tone="purple" />
      </section>
      <section className="dashboard-grid">
        <article className="panel span-two"><div className="panel-header"><div><h2>Next appointment</h2><p>Your upcoming care visit</p></div></div>{next ? <div className="next-appointment"><div className="calendar-tile"><strong>{new Date(next.startsAt).getDate()}</strong><span>{new Date(next.startsAt).toLocaleDateString([], { month: 'short' })}</span></div><div><h3>{next.type}</h3><p>{next.doctorName}</p><small>{new Date(next.startsAt).toLocaleString([], { dateStyle: 'full', timeStyle: 'short' })}</small></div><span className={`status-badge ${next.status.toLowerCase()}`}>{next.status}</span></div> : <div className="empty-state"><CalendarCheck /><strong>No appointment scheduled</strong><button className="text-button" type="button" onClick={() => window.location.hash = '#/patient/book'}>Book your next visit</button></div>}</article>
        <article className="panel"><div className="panel-header"><div><h2>Health profile</h2><p>Information shared with your clinic</p></div></div><div className="profile-facts"><div><span>Blood type</span><strong>O+</strong></div><div><span>Risk level</span><strong className={`risk ${selectedPatient.riskLevel.toLowerCase()}`}>{selectedPatient.riskLevel}</strong></div><div><span>Conditions</span><strong>{selectedPatient.conditions.join(', ')}</strong></div><div><span>Allergies</span><strong>{selectedPatient.allergies.join(', ')}</strong></div></div></article>
      </section>
    </>
  );
}
