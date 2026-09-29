import { Download, Search, ShieldCheck } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function AuditLogs() {
  const { auditEvents } = useApp();
  const seeded = [
    ...auditEvents,
    { id: 'a2', actor: 'Dr. Lina Haddad', action: 'Viewed patient record', resource: 'Patient p-1002', occurredAt: new Date(Date.now() - 2400000).toISOString() },
    { id: 'a3', actor: 'Clinic Admin', action: 'Updated user permissions', resource: 'User u-doctor', occurredAt: new Date(Date.now() - 7200000).toISOString() },
    { id: 'a4', actor: 'Omar Nasser', action: 'Requested appointment', resource: 'Appointment a-502', occurredAt: new Date(Date.now() - 14400000).toISOString() }
  ];
  return (
    <>
      <PageHeader title="Audit logs" description="Trace security-sensitive actions and clinical data access." action={<button className="secondary-button" type="button"><Download size={17} /> Export CSV</button>} />
      <article className="compliance-callout"><ShieldCheck /><div><strong>Immutable activity trail</strong><p>Audit events support privacy reviews and operational investigations. Demo events reset when the backend restarts.</p></div></article>
      <article className="panel"><div className="table-toolbar"><label className="search-field"><Search size={17} /><input placeholder="Search activity" /></label><div className="form-pair audit-filters"><select><option>All actions</option><option>Authentication</option><option>Clinical access</option><option>Administration</option></select><input type="date" /></div></div><div className="responsive-table"><table><thead><tr><th>Time</th><th>Actor</th><th>Action</th><th>Resource</th><th>Result</th></tr></thead><tbody>{seeded.map((event) => <tr key={event.id}><td><time>{new Date(event.occurredAt).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' })}</time></td><td><strong>{event.actor}</strong></td><td>{event.action}</td><td><code>{event.resource}</code></td><td><span className="active-state"><i /> Success</span></td></tr>)}</tbody></table></div></article>
    </>
  );
}
