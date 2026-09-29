import { Activity, CalendarCheck, Server, ShieldCheck, Users } from 'lucide-react';
import { MetricCard } from '../../components/MetricCard';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function AdminDashboard() {
  const { users, patients, appointments, apiStatus, auditEvents } = useApp();
  return (
    <>
      <PageHeader title="Operations overview" description="Monitor platform activity, access, and clinic performance." action={<button className="primary-button" type="button" onClick={() => window.location.hash = '#/admin/users'}>Manage users</button>} />
      <section className="stat-grid">
        <MetricCard label="Platform users" value={users.length || 3} detail="Across all roles" icon={<Users />} />
        <MetricCard label="Active patients" value={patients.length} detail="Registered profiles" icon={<Activity />} tone="blue" />
        <MetricCard label="Appointments" value={appointments.length} detail="In current schedule" icon={<CalendarCheck />} tone="amber" />
        <MetricCard label="API status" value={apiStatus === 'Connected' ? 'Healthy' : 'Demo'} detail="Core services" icon={<Server />} tone="purple" />
      </section>
      <section className="dashboard-grid">
        <article className="panel span-two"><div className="panel-header"><div><h2>Operational activity</h2><p>Appointments created over the current period</p></div><select className="compact-select"><option>Last 7 days</option><option>Last 30 days</option></select></div><div className="area-chart"><div className="chart-grid" /><svg viewBox="0 0 800 220" preserveAspectRatio="none" aria-label="Appointment activity chart"><defs><linearGradient id="chartFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#0f766e" stopOpacity=".28"/><stop offset="100%" stopColor="#0f766e" stopOpacity="0"/></linearGradient></defs><path className="area" d="M0 190 C80 180 100 120 180 138 S310 180 390 100 S520 40 590 90 S710 150 800 35 L800 220 L0 220Z"/><path className="line" d="M0 190 C80 180 100 120 180 138 S310 180 390 100 S520 40 590 90 S710 150 800 35"/></svg><div className="chart-labels"><span>Mon</span><span>Tue</span><span>Wed</span><span>Thu</span><span>Fri</span><span>Sat</span><span>Sun</span></div></div></article>
        <article className="panel system-health"><div className="panel-header"><div><h2>System health</h2><p>Service availability</p></div><ShieldCheck /></div>{['REST API', 'Patient portal', 'Clinical workspace', 'Notification queue'].map((service) => <div key={service}><span><i className="status-dot online" /> {service}</span><strong>Operational</strong></div>)}</article>
        <article className="panel span-two"><div className="panel-header"><div><h2>Recent activity</h2><p>Latest security and workflow events</p></div><button className="text-button" type="button" onClick={() => window.location.hash = '#/admin/audit'}>View audit log</button></div><div className="activity-table">{auditEvents.slice(0, 5).map((event) => <div key={event.id}><span className="activity-icon"><Activity size={16} /></span><span><strong>{event.action}</strong><small>{event.actor} · {event.resource}</small></span><time>{new Date(event.occurredAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</time></div>)}</div></article>
        <article className="panel"><h2>Role distribution</h2><div className="donut-wrap"><div className="donut"><span>{users.length || 3}<small>users</small></span></div><div className="legend"><span><i className="teal" /> Doctors <strong>{users.filter((item) => item.role === 'DOCTOR').length || 1}</strong></span><span><i className="blue" /> Patients <strong>{users.filter((item) => item.role === 'PATIENT').length || 1}</strong></span><span><i className="purple" /> Admins <strong>{users.filter((item) => item.role === 'ADMIN').length || 1}</strong></span></div></div></article>
      </section>
    </>
  );
}
