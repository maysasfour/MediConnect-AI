import { FormEvent, useState } from 'react';
import { HeartPulse, LockKeyhole, ShieldCheck } from 'lucide-react';
import { useApp } from '../../context/AppContext';

export default function Login() {
  const { signIn, loading } = useApp();
  const [email, setEmail] = useState('doctor@mediconnect.ai');
  const [password, setPassword] = useState('doctor123');
  const [error, setError] = useState('');

  async function submit(event: FormEvent) {
    event.preventDefault();
    setError('');
    try {
      await signIn(email, password);
      const role = email.startsWith('admin') ? 'admin' : email.startsWith('patient') ? 'patient' : 'doctor';
      window.location.hash = `#/${role}`;
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Unable to sign in.');
    }
  }

  function useDemo(role: 'doctor' | 'admin' | 'patient') {
    setEmail(`${role}@mediconnect.ai`);
    setPassword(`${role}123`);
  }

  return (
    <main className="auth-page login-layout">
      <section className="auth-story">
        <div className="brand auth-brand"><span className="brand-mark"><HeartPulse /></span><div><span>MediConnect</span><small>Connected care</small></div></div>
        <div className="story-content">
          <span className="hero-badge"><ShieldCheck size={16} /> Private by design</span>
          <h1>Healthcare feels simpler when everything connects.</h1>
          <p>One secure workspace for appointments, clinical records, prescriptions, and carefully designed AI support.</p>
          <div className="trust-row"><strong>24/7</strong><span>Patient access</span><strong>3 roles</strong><span>One coordinated platform</span></div>
        </div>
        <p className="auth-footnote">Demo environment · No real patient data</p>
      </section>
      <section className="auth-form-wrap">
        <form className="auth-card" onSubmit={submit}>
          <div className="auth-icon"><LockKeyhole /></div>
          <p className="eyebrow">Welcome back</p>
          <h2>Sign in to your workspace</h2>
          <p className="muted">Choose a demo role or enter your account details.</p>
          <div className="demo-roles">
            <button type="button" onClick={() => useDemo('doctor')}>Doctor</button>
            <button type="button" onClick={() => useDemo('patient')}>Patient</button>
            <button type="button" onClick={() => useDemo('admin')}>Admin</button>
          </div>
          <label>Email address<input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required /></label>
          <label>Password<input type="password" value={password} onChange={(event) => setPassword(event.target.value)} required /></label>
          <div className="form-meta"><label className="check-label"><input type="checkbox" defaultChecked /> Remember me</label><button className="text-button" type="button" onClick={() => window.location.hash = '#/forgot-password'}>Forgot password?</button></div>
          {error && <p className="form-error">{error}</p>}
          <button className="primary-button wide" disabled={loading} type="submit">{loading ? 'Signing in…' : 'Sign in securely'}</button>
          <p className="auth-switch">New to MediConnect? <button className="text-button" type="button" onClick={() => window.location.hash = '#/register'}>Create an account</button></p>
        </form>
      </section>
    </main>
  );
}
