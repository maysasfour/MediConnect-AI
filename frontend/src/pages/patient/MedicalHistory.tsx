import { Download, FileHeart, LockKeyhole } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function MedicalHistory() {
  const { records, selectedPatient } = useApp();
  return (
    <>
      <PageHeader title="Medical history" description="A secure timeline of clinical encounters shared by your care team." action={<button className="secondary-button" type="button"><Download size={17} /> Export summary</button>} />
      <article className="privacy-banner"><LockKeyhole /><div><strong>Your information is private</strong><p>Only authorized members of your care team can access these records.</p></div></article>
      <section className="history-layout">
        <aside className="panel profile-summary"><span className="large-avatar">{selectedPatient.name.split(' ').map((part) => part[0]).join('').slice(0, 2)}</span><h2>{selectedPatient.name}</h2><p>{selectedPatient.age} years · {selectedPatient.gender}</p><div className="profile-facts"><div><span>Patient ID</span><strong>{selectedPatient.id}</strong></div><div><span>Phone</span><strong>{selectedPatient.phone}</strong></div><div><span>Risk profile</span><strong className={`risk ${selectedPatient.riskLevel.toLowerCase()}`}>{selectedPatient.riskLevel}</strong></div></div><h3>Known conditions</h3><div className="tag-list">{selectedPatient.conditions.map((item) => <span key={item}>{item}</span>)}</div><h3>Allergies</h3><div className="tag-list warning">{selectedPatient.allergies.map((item) => <span key={item}>{item}</span>)}</div></aside>
        <article className="panel"><div className="panel-header"><div><h2>Encounter timeline</h2><p>{records.length} clinical records</p></div><FileHeart /></div><div className="medical-timeline">{records.map((record) => <div className="medical-event" key={record.id}><span className="event-dot" /><time>{new Date(record.date).toLocaleDateString([], { month: 'long', day: 'numeric', year: 'numeric' })}</time><div><span className="status-badge completed">Completed visit</span><h3>{record.diagnosis}</h3><p className="doctor-name">{record.doctorName}</p><p>{record.notes}</p>{record.prescriptions.length > 0 && <div className="event-prescriptions"><strong>Medication plan</strong>{record.prescriptions.map((item) => <span key={item}>{item}</span>)}</div>}</div></div>)}{records.length === 0 && <div className="empty-state"><FileHeart /><h2>No medical records yet</h2><p>Your completed clinic visits will appear here.</p></div>}</div></article>
      </section>
    </>
  );
}
