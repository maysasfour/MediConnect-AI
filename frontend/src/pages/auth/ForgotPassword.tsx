import { FormEvent, useState } from 'react';
import { HeartPulse, MailCheck } from 'lucide-react';

export default function ForgotPassword() {
  const [sent, setSent] = useState(false);

  function submit(event: FormEvent) {
    event.preventDefault();
    setSent(true);
  }

  return (
    <main className="auth-page simple-auth">
      <button className="brand auth-brand-link" type="button" onClick={() => window.location.hash = '#/login'}><span className="brand-mark"><HeartPulse /></span><div><span>MediConnect</span><small>Connected care</small></div></button>
      <form className="auth-card" onSubmit={submit}>
        <div className="auth-icon"><MailCheck /></div>
        <p className="eyebrow">Account recovery</p>
        <h1>Reset your password</h1>
        {sent ? (
          <div className="success-message"><strong>Check your inbox</strong><p>For this demo, the recovery flow is complete. Return to sign in with a demo account.</p></div>
        ) : (
          <><p className="muted">Enter your email and we’ll send password recovery instructions.</p><label>Email address<input type="email" placeholder="you@example.com" required /></label><button className="primary-button wide" type="submit">Send reset instructions</button></>
        )}
        <button className="text-button centered" type="button" onClick={() => window.location.hash = '#/login'}>Back to sign in</button>
      </form>
    </main>
  );
}
