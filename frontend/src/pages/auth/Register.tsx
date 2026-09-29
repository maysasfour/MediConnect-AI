import { FormEvent, useState } from 'react';
import { HeartPulse, UserPlus } from 'lucide-react';
import { useApp } from '../../context/AppContext';

export default function Register() {
  const { signUp, loading } = useApp();
  const [error, setError] = useState('');

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const password = String(form.get('password'));
    if (password !== String(form.get('confirmation'))) {
      setError('Passwords do not match.');
      return;
    }
    try {
      await signUp(String(form.get('name')), String(form.get('email')), password);
      window.location.hash = '#/patient';
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Registration failed.');
    }
  }

  return (
    <main className="auth-page simple-auth">
      <button className="brand auth-brand-link" type="button" onClick={() => window.location.hash = '#/login'}><span className="brand-mark"><HeartPulse /></span><div><span>MediConnect</span><small>Connected care</small></div></button>
      <form className="auth-card" onSubmit={submit}>
        <div className="auth-icon"><UserPlus /></div>
        <p className="eyebrow">Patient registration</p>
        <h1>Create your account</h1>
        <p className="muted">Manage appointments and access your health information securely.</p>
        <label>Full name<input name="name" placeholder="Your full name" required /></label>
        <label>Email address<input name="email" type="email" placeholder="you@example.com" required /></label>
        <div className="form-pair"><label>Password<input name="password" type="password" minLength={8} required /></label><label>Confirm password<input name="confirmation" type="password" minLength={8} required /></label></div>
        <label className="check-label consent"><input type="checkbox" required /> I agree to the privacy notice and demo terms.</label>
        {error && <p className="form-error">{error}</p>}
        <button className="primary-button wide" disabled={loading} type="submit">{loading ? 'Creating account…' : 'Create patient account'}</button>
        <p className="auth-switch">Already registered? <button className="text-button" type="button" onClick={() => window.location.hash = '#/login'}>Sign in</button></p>
      </form>
    </main>
  );
}
