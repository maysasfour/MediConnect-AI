import { FormEvent, useState } from 'react';
import { Bot, CircleAlert, Send, ShieldCheck, Sparkles } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';
import type { AiSummary } from '../../services/aiService';

export default function AIAssistant() {
  const { selectedPatient, summarizeSymptoms } = useApp();
  const [message, setMessage] = useState('I have had a mild cough, fever, and fatigue for two days.');
  const [summary, setSummary] = useState<AiSummary | null>(null);
  const [loading, setLoading] = useState(false);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    try { setSummary(await summarizeSymptoms(message, selectedPatient.id)); }
    finally { setLoading(false); }
  }

  return (
    <>
      <PageHeader title="Health information assistant" description="Organize your symptoms before speaking with a qualified clinician." />
      <article className="ai-disclaimer"><ShieldCheck /><div><strong>Designed for safe preparation, not diagnosis</strong><p>This tool cannot diagnose illness or replace professional care. For emergencies, contact local emergency services now.</p></div></article>
      <section className="assistant-layout">
        <article className="panel assistant-chat"><div className="assistant-intro"><span><Bot /></span><div><p className="eyebrow">MediConnect assistant</p><h2>How are you feeling today?</h2><p>Describe your symptoms, when they started, and how severe they feel.</p></div></div><div className="prompt-chips"><button type="button" onClick={() => setMessage('I have a headache and dizziness since this morning.')}>Headache and dizziness</button><button type="button" onClick={() => setMessage('I have had a cough and fever for two days.')}>Cough and fever</button><button type="button" onClick={() => setMessage('I need help preparing for my follow-up appointment.')}>Prepare for follow-up</button></div><form className="assistant-input" onSubmit={submit}><textarea value={message} onChange={(event) => setMessage(event.target.value)} maxLength={800} required /><div><span>{message.length}/800</span><button className="primary-button" disabled={loading} type="submit"><Send size={17} /> {loading ? 'Organizing…' : 'Create summary'}</button></div></form></article>
        <aside className="assistant-side">
          {summary ? <article className="panel ai-output"><div className="ai-output-title"><Sparkles /><div><p className="eyebrow">Prepared summary</p><h2>Bring this to your clinician</h2></div></div><p className="summary-text">{summary.summary}</p><h3>Safety notes</h3>{summary.safetyNotes.map((note) => <div className="safety-note" key={note}><CircleAlert size={16} /> {note}</div>)}<h3>Suggested next steps</h3><ol>{summary.suggestedNextSteps.map((step) => <li key={step}>{step}</li>)}</ol><button className="secondary-button wide" type="button" onClick={() => window.location.hash = '#/patient/book'}>Book clinician visit</button></article> : <article className="panel assistant-placeholder"><Sparkles /><h2>Your structured summary will appear here</h2><p>It will highlight symptom details, safety information, and practical next steps.</p></article>}
        </aside>
      </section>
    </>
  );
}
