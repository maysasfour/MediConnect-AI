import { CalendarDays, Clock3, MapPin } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function Appointments() {
  const { appointments, selectedPatient } = useApp();
  const mine = appointments.filter((item) => item.patientId === selectedPatient.id);
  return (
    <>
      <PageHeader title="My appointments" description="Review upcoming visits and your recent appointment history." action={<button className="primary-button" type="button" onClick={() => window.location.hash = '#/patient/book'}>Book new appointment</button>} />
      <div className="filter-tabs"><button className="active" type="button">Upcoming</button><button type="button">Past visits</button><button type="button">Cancelled</button></div>
      <section className="appointment-cards">
        {mine.map((appointment) => <article className="panel appointment-card" key={appointment.id}><div className="calendar-tile"><strong>{new Date(appointment.startsAt).getDate()}</strong><span>{new Date(appointment.startsAt).toLocaleDateString([], { month: 'short' })}</span></div><div className="appointment-info"><div><span className={`status-badge ${appointment.status.toLowerCase()}`}>{appointment.status}</span><h2>{appointment.type}</h2><p>{appointment.doctorName}</p></div><div className="appointment-meta"><span><Clock3 size={16} /> {new Date(appointment.startsAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span><span><CalendarDays size={16} /> {new Date(appointment.startsAt).toLocaleDateString([], { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</span><span><MapPin size={16} /> MediConnect Amman · Room 4</span></div></div><div className="appointment-actions"><button className="secondary-button" type="button">Reschedule</button><button className="text-button danger-text" type="button">Cancel</button></div></article>)}
        {mine.length === 0 && <article className="panel empty-state"><CalendarDays /><h2>No appointments yet</h2><p>Book a visit with one of our available doctors.</p><button className="primary-button" type="button" onClick={() => window.location.hash = '#/patient/book'}>Book appointment</button></article>}
      </section>
    </>
  );
}
