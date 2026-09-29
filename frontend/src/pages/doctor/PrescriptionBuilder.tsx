import { FormEvent, useState } from 'react';
import { Pill, Plus, Trash2 } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';
import type { PrescriptionItem } from '../../types/prescription';

const emptyItem = (): PrescriptionItem => ({ medicationName: '', dosage: '', frequency: 'Once daily', durationDays: 7, instructions: '' });

export default function PrescriptionBuilder() {
  const { patients, selectedPatientId, selectPatient, prescribe } = useApp();
  const [items, setItems] = useState<PrescriptionItem[]>([emptyItem()]);
  const [submitting, setSubmitting] = useState(false);

  function updateItem(index: number, field: keyof PrescriptionItem, value: string | number) {
    setItems((current) => current.map((item, itemIndex) => itemIndex === index ? { ...item, [field]: value } : item));
  }

  async function submit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    try {
      await prescribe(selectedPatientId, items);
      setItems([emptyItem()]);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      <PageHeader title="Prescription builder" description="Create a clear, structured medication plan for your patient." />
      <form className="prescription-builder" onSubmit={submit}>
        <article className="panel"><div className="panel-header"><div><h2>Patient and prescriber</h2><p>Prescription header information</p></div><Pill /></div><div className="form-pair"><label>Patient<select value={selectedPatientId} onChange={(event) => selectPatient(event.target.value)}>{patients.map((patient) => <option key={patient.id} value={patient.id}>{patient.name}</option>)}</select></label><label>Prescription date<input value={new Date().toISOString().slice(0, 10)} readOnly /></label></div></article>
        {items.map((item, index) => <article className="panel medication-editor" key={index}><div className="panel-header"><div><h2>Medication {index + 1}</h2><p>Dosage and patient instructions</p></div>{items.length > 1 && <button className="icon-button danger" type="button" onClick={() => setItems((current) => current.filter((_, itemIndex) => itemIndex !== index))}><Trash2 size={18} /></button>}</div><div className="medication-grid"><label className="span-two">Medication name<input value={item.medicationName} onChange={(event) => updateItem(index, 'medicationName', event.target.value)} placeholder="e.g. Amlodipine" required /></label><label>Dosage<input value={item.dosage} onChange={(event) => updateItem(index, 'dosage', event.target.value)} placeholder="e.g. 5 mg" required /></label><label>Frequency<select value={item.frequency} onChange={(event) => updateItem(index, 'frequency', event.target.value)}><option>Once daily</option><option>Twice daily</option><option>Every 8 hours</option><option>As needed</option></select></label><label>Duration (days)<input type="number" min="1" value={item.durationDays} onChange={(event) => updateItem(index, 'durationDays', Number(event.target.value))} required /></label><label className="span-two">Instructions<input value={item.instructions} onChange={(event) => updateItem(index, 'instructions', event.target.value)} placeholder="Take after food, monitoring notes…" /></label></div></article>)}
        <div className="builder-actions"><button className="secondary-button" type="button" onClick={() => setItems((current) => [...current, emptyItem()])}><Plus size={17} /> Add medication</button><button className="primary-button" type="submit" disabled={submitting}>{submitting ? 'Saving…' : 'Review and issue prescription'}</button></div>
      </form>
    </>
  );
}
