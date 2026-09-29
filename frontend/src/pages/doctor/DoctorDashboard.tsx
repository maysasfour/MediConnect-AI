import { Activity, CalendarClock, CircleAlert, Users } from 'lucide-react';
import { MetricCard } from '../../components/MetricCard';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function DoctorDashboard() {
  const { appointments, patients, selectPatient } = useApp();
  const today = new Date().toDateString();
  const todayAppointments = appointments.filter((item) => new Date(item.startsAt).toDateString() === today);
  const upcoming = appointments.slice().sort((a, b) => new Date(a.startsAt).getTime() - new Date(b.startsAt).getTime());

  return (
    <>
      <PageHeader title="Good day, Doctor" description="Here’s what needs your attention across the clinic today." action={<button className="primary-button" type="button" onClick={() => window.location.hash = '#/doctor/consultation'}>Start consultation</button>} />
      <section className="stat-grid">
        <MetricCard label="Active patients" value={patients.length} detail="Across your care panel" icon={<Users />} />
        <MetricCard label="Today’s visits" value={todayAppointments.length} detail="Scheduled consultations" icon={<CalendarClock />} tone="blue" />
        <MetricCard label="Pending requests" value={appointments.filter((item) => item.status === 'Pending').length} detail="Awaiting confirmation" icon={<Activity />} tone="amber" />
        <MetricCard label="High-risk patients" value={patients.filter((item) => item.riskLevel === 'High').length} detail="Review recommended" icon={<CircleAlert />} tone="red" />
      </section>
      <section className="dashboard-grid">
        <article className="panel span-two">
          <div className="panel-header"><div><h2>Upcoming schedule</h2><p>Your next patient visits</p></div><button className="text-button" type="button" onClick={() => window.location.hash = '#/doctor/consultation'}>View workspace</button></div>
          <div className="timeline-list">
            {upcoming.map((appointment) => (
              <button className="timeline-item" key={appointment.id} type="button" onClick={() => { selectPatient(appointment.patientId); window.location.hash = '#/doctor/consultation'; }}>
                <time>{new Date(appointment.startsAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</time>
                <span className="timeline-line" />
                <span className="patient-avatar">{appointment.patientName.split(' ').map((part) => part[0]).join('').slice(0, 2)}</span>
                <span className="timeline-copy"><strong>{appointment.patientName}</strong><small>{appointment.type} · {appointment.doctorName}</small></span>
                <span className={`status-badge ${appointment.status.toLowerCase()}`}>{appointment.status}</span>
              </button>
            ))}
          </div>
        </article>
        <article className="panel">
          <div className="panel-header"><div><h2>Priority patients</h2><p>Based on current risk level</p></div></div>
          <div className="compact-list">
            {patients.slice().sort((a, b) => (a.riskLevel === 'High' ? -1 : b.riskLevel === 'High' ? 1 : 0)).map((patient) => (
              <button key={patient.id} type="button" onClick={() => { selectPatient(patient.id); window.location.hash = '#/doctor/patients'; }}>
                <span className="patient-avatar soft">{patient.name.split(' ').map((part) => part[0]).join('').slice(0, 2)}</span>
                <span><strong>{patient.name}</strong><small>{patient.conditions.join(', ')}</small></span>
                <span className={`risk ${patient.riskLevel.toLowerCase()}`}>{patient.riskLevel}</span>
              </button>
            ))}
          </div>
        </article>
      </section>
    </>
  );
}
