import { FormEvent, useState } from 'react';
import { CalendarPlus, CheckCircle2 } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function BookAppointment() {
  const { selectedPatient, bookVisit } = useApp();
  const [complete, setComplete] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    setSubmitting(true);
    try {
      await bookVisit({ patientId: selectedPatient.id, doctorName: String(form.get('doctor')), type: String(form.get('type')), startsAt: `${form.get('date')}T${form.get('time')}:00` });
      setComplete(true);
    } finally { setSubmitting(false); }
  }

  if (complete) return <article className="panel booking-success"><CheckCircle2 /><p className="eyebrow">Request received</p><h1>Your appointment is pending confirmation</h1><p>We’ll notify you when the clinic confirms your requested time.</p><button className="primary-button" type="button" onClick={() => window.location.hash = '#/patient/appointments'}>View appointments</button><button className="text-button" type="button" onClick={() => setComplete(false)}>Book another visit</button></article>;

  return (
    <>
      <PageHeader title="Book an appointment" description="Choose a specialty, doctor, and time that works for you." />
      <form className="booking-workflow" onSubmit={submit}>
        <article className="panel booking-main"><div className="form-step"><span>1</span><div><h2>Visit details</h2><p>Tell us what kind of care you need.</p></div></div><label>Visit type<select name="type" required><option>General consultation</option><option>Follow-up</option><option>Respiratory consult</option><option>Chronic care review</option><option>Lab results review</option></select></label><label>Brief reason for visit<textarea placeholder="Share symptoms or concerns to help your doctor prepare." /></label><div className="form-step"><span>2</span><div><h2>Choose your doctor</h2><p>Select an available clinician.</p></div></div><div className="doctor-options"><label><input type="radio" name="doctor" value="Dr. Lina Haddad" defaultChecked /><span className="patient-avatar">LH</span><span><strong>Dr. Lina Haddad</strong><small>Family medicine · Next available today</small></span></label><label><input type="radio" name="doctor" value="Dr. Samer Khoury" /><span className="patient-avatar">SK</span><span><strong>Dr. Samer Khoury</strong><small>Pulmonology · Next available tomorrow</small></span></label><label><input type="radio" name="doctor" value="Dr. Reem Mansour" /><span className="patient-avatar">RM</span><span><strong>Dr. Reem Mansour</strong><small>Internal medicine · Next available Sunday</small></span></label></div><div className="form-step"><span>3</span><div><h2>Select date and time</h2><p>Your request will be confirmed by the clinic.</p></div></div><div className="form-pair"><label>Preferred date<input name="date" type="date" min={new Date().toISOString().slice(0, 10)} defaultValue={new Date(Date.now() + 86400000).toISOString().slice(0, 10)} required /></label><label>Preferred time<select name="time"><option value="09:00">9:00 AM</option><option value="10:30">10:30 AM</option><option value="13:00">1:00 PM</option><option value="15:30">3:30 PM</option></select></label></div></article>
        <aside className="panel booking-summary"><CalendarPlus /><h2>Booking summary</h2><div><span>Patient</span><strong>{selectedPatient.name}</strong></div><div><span>Location</span><strong>MediConnect Amman</strong></div><div><span>Estimated duration</span><strong>30 minutes</strong></div><p>No payment is collected for this demo appointment.</p><button className="primary-button wide" disabled={submitting} type="submit">{submitting ? 'Submitting…' : 'Request appointment'}</button></aside>
      </form>
    </>
  );
}
