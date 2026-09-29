import { ClipboardPlus, Phone, Search } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function PatientProfile() {
  const { patients, selectedPatient, selectedPatientId, selectPatient, records, prescriptions } = useApp();
  return (
    <>
      <PageHeader title="Patient directory" description="Review demographics, risks, clinical history, and active treatment." action={<button className="primary-button" type="button" onClick={() => window.location.hash = '#/doctor/consultation'}><ClipboardPlus size={17} /> New consultation</button>} />
      <section className="split-workspace">
        <article className="panel patient-directory">
          <label className="search-field"><Search size={17} /><input placeholder="Search patients" /></label>
          <div className="patient-list">
            {patients.map((patient) => <button className={patient.id === selectedPatientId ? 'patient-card selected' : 'patient-card'} key={patient.id} type="button" onClick={() => selectPatient(patient.id)}><span className="patient-avatar">{patient.name.split(' ').map((part) => part[0]).join('').slice(0, 2)}</span><span><strong>{patient.name}</strong><small>{patient.age} years · {patient.gender}</small></span><span className={`risk ${patient.riskLevel.toLowerCase()}`}>{patient.riskLevel}</span></button>)}
          </div>
        </article>
        <div className="profile-column">
          <article className="panel profile-hero">
            <span className="large-avatar">{selectedPatient.name.split(' ').map((part) => part[0]).join('').slice(0, 2)}</span>
            <div><span className={`risk ${selectedPatient.riskLevel.toLowerCase()}`}>{selectedPatient.riskLevel} risk</span><h2>{selectedPatient.name}</h2><p>{selectedPatient.age} years · {selectedPatient.gender} · ID {selectedPatient.id}</p></div>
            <a className="secondary-button" href={`tel:${selectedPatient.phone}`}><Phone size={16} /> {selectedPatient.phone}</a>
          </article>
          <section className="detail-grid">
            <article className="panel"><h3>Conditions</h3><div className="tag-list">{selectedPatient.conditions.map((condition) => <span key={condition}>{condition}</span>)}</div></article>
            <article className="panel"><h3>Allergies</h3><div className="tag-list warning">{selectedPatient.allergies.map((allergy) => <span key={allergy}>{allergy}</span>)}</div></article>
          </section>
          <article className="panel"><div className="panel-header"><div><h2>Clinical timeline</h2><p>Recent encounters and prescriptions</p></div></div><div className="record-list">{records.map((record) => <div className="record" key={record.id}><span className="record-date">{new Date(record.date).toLocaleDateString([], { month: 'short', day: 'numeric', year: 'numeric' })}</span><div><strong>{record.diagnosis}</strong><small>{record.doctorName}</small><p>{record.notes}</p>{record.prescriptions.map((item) => <span className="medication-chip" key={item}>{item}</span>)}</div></div>)}{records.length === 0 && <p className="empty-state">No encounters recorded for this patient.</p>}</div></article>
          {prescriptions.length > 0 && <article className="panel"><h3>Active prescriptions</h3>{prescriptions.map((prescription) => <div className="prescription-row" key={prescription.id}><strong>{prescription.items.map((item) => item.medicationName).join(', ')}</strong><span>{prescription.prescribedOn}</span><span className="status-badge confirmed">{prescription.status}</span></div>)}</article>}
        </div>
      </section>
    </>
  );
}
