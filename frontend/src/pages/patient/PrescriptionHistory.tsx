import { CalendarDays, Pill } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function PrescriptionHistory() {
  const { prescriptions, records } = useApp();
  const legacy = records.flatMap((record) => record.prescriptions.map((medication, index) => ({ id: `${record.id}-${index}`, medication, doctor: record.doctorName, date: record.date })));
  return (
    <>
      <PageHeader title="My prescriptions" description="Review active medication instructions and previous prescriptions." />
      <div className="filter-tabs"><button className="active" type="button">Active</button><button type="button">Past prescriptions</button></div>
      <section className="prescription-list">
        {prescriptions.map((prescription) => <article className="panel prescription-card" key={prescription.id}><div className="rx-symbol">Rx</div><div className="rx-details"><div><span className="status-badge confirmed">{prescription.status}</span><h2>{prescription.items.map((item) => item.medicationName).join(', ')}</h2><p>Prescribed by {prescription.doctorName}</p></div>{prescription.items.map((item, index) => <div className="dosage-grid" key={index}><div><span>Dosage</span><strong>{item.dosage}</strong></div><div><span>Frequency</span><strong>{item.frequency}</strong></div><div><span>Duration</span><strong>{item.durationDays} days</strong></div><div><span>Instructions</span><strong>{item.instructions || 'Follow clinician guidance'}</strong></div></div>)}</div><div className="rx-date"><CalendarDays /><span>Issued</span><strong>{prescription.prescribedOn}</strong></div></article>)}
        {legacy.map((item) => <article className="panel prescription-card" key={item.id}><div className="rx-symbol"><Pill /></div><div className="rx-details"><div><span className="status-badge confirmed">Active</span><h2>{item.medication}</h2><p>Prescribed by {item.doctor}</p></div><div className="dosage-grid"><div><span>Instructions</span><strong>Use exactly as directed by your clinician.</strong></div></div></div><div className="rx-date"><CalendarDays /><span>Issued</span><strong>{item.date}</strong></div></article>)}
        {prescriptions.length === 0 && legacy.length === 0 && <article className="panel empty-state"><Pill /><h2>No prescriptions available</h2><p>Medication orders from your clinician will appear here.</p></article>}
      </section>
    </>
  );
}
