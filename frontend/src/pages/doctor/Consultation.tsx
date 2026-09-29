import { FormEvent, useState } from 'react';
import { Activity, Brain, Save, Stethoscope } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function Consultation() {
  const { patients, selectedPatient, selectedPatientId, selectPatient, notify } = useApp();
  const [saved, setSaved] = useState(false);

  function submit(event: FormEvent) {
    event.preventDefault();
    setSaved(true);
    notify('Consultation note saved to the demo chart.');
  }

  return (
    <>
      <PageHeader title="Consultation workspace" description="Document the encounter with a focused, clinician-first workflow." />
      <form className="consultation-layout" onSubmit={submit}>
        <div className="consult-main">
          <article className="panel patient-strip"><label>Patient<select value={selectedPatientId} onChange={(event) => selectPatient(event.target.value)}>{patients.map((patient) => <option key={patient.id} value={patient.id}>{patient.name}</option>)}</select></label><div><strong>{selectedPatient.age} years · {selectedPatient.gender}</strong><span>{selectedPatient.conditions.join(', ')}</span></div><span className={`risk ${selectedPatient.riskLevel.toLowerCase()}`}>{selectedPatient.riskLevel} risk</span></article>
          <article className="panel"><div className="panel-header"><div><h2><Activity size={19} /> Vitals</h2><p>Record current observations</p></div></div><div className="vitals-grid"><label>Blood pressure<input placeholder="120/80" /></label><label>Heart rate<input type="number" placeholder="72" /></label><label>Temperature °C<input type="number" step="0.1" placeholder="36.8" /></label><label>SpO₂ %<input type="number" placeholder="98" /></label><label>Weight kg<input type="number" step="0.1" placeholder="74" /></label></div></article>
          <article className="panel note-editor"><div className="panel-header"><div><h2><Stethoscope size={19} /> Clinical note</h2><p>SOAP-style encounter documentation</p></div></div><label>Chief complaint<input required placeholder="Reason for today’s visit" /></label><label>History and examination<textarea required placeholder="Symptoms, history, examination findings…" /></label><div className="form-pair"><label>Assessment<input required placeholder="Clinical assessment" /></label><label>Diagnosis code<input placeholder="e.g. I10" /></label></div><label>Care plan<textarea required placeholder="Treatment plan, follow-up, and patient guidance…" /></label></article>
          <div className="form-actions"><span>{saved ? 'All changes saved.' : 'Draft is stored while this page is open.'}</span><button className="secondary-button" type="button" onClick={() => window.location.hash = '#/doctor/prescriptions'}>Create prescription</button><button className="primary-button" type="submit"><Save size={17} /> Save encounter</button></div>
        </div>
        <aside className="consult-aside"><article className="panel safety-card"><Brain /><h3>Clinical assist</h3><p>AI suggestions are informational only. Confirm every clinical decision independently.</p><button className="secondary-button wide" type="button" onClick={() => notify('Safety guidance acknowledged. Continue with independent clinical judgment.')}>Review safety guidance</button></article><article className="panel"><h3>Known allergies</h3><div className="tag-list warning">{selectedPatient.allergies.map((item) => <span key={item}>{item}</span>)}</div></article><article className="panel"><h3>Active conditions</h3><div className="tag-list">{selectedPatient.conditions.map((item) => <span key={item}>{item}</span>)}</div></article></aside>
      </form>
    </>
  );
}
