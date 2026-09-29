import { Activity, CalendarCheck, TrendingUp, Users } from 'lucide-react';
import { MetricCard } from '../../components/MetricCard';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function Analytics() {
  const { patients, appointments } = useApp();
  const values = [42, 68, 54, 81, 73, 92, 76];
  return (
    <>
      <PageHeader title="Clinic analytics" description="Understand patient demand and operational performance." action={<select className="compact-select"><option>Last 7 days</option><option>Last 30 days</option><option>This quarter</option></select>} />
      <section className="stat-grid"><MetricCard label="Total patients" value={patients.length} detail="Current care panel" icon={<Users />} /><MetricCard label="Appointments" value={appointments.length} detail="Scheduled activity" icon={<CalendarCheck />} tone="blue" /><MetricCard label="Completion rate" value="91%" detail="+4.2% this month" icon={<TrendingUp />} tone="purple" /><MetricCard label="Avg. wait time" value="12m" detail="2m faster than target" icon={<Activity />} tone="amber" /></section>
      <section className="analytics-grid"><article className="panel span-two"><div className="panel-header"><div><h2>Appointment volume</h2><p>Daily scheduled visits</p></div></div><div className="bar-chart">{values.map((value, index) => <div key={index}><span style={{ height: `${value}%` }}><i>{Math.round(value / 7)}</i></span><small>{['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'][index]}</small></div>)}</div></article><article className="panel"><h2>Patient risk mix</h2><div className="risk-breakdown">{['Low', 'Medium', 'High'].map((risk) => { const count = patients.filter((patient) => patient.riskLevel === risk).length; const percent = patients.length ? Math.round(count / patients.length * 100) : 0; return <div key={risk}><span><strong>{risk} risk</strong><small>{count} patients</small></span><b>{percent}%</b><div><i className={risk.toLowerCase()} style={{ width: `${percent}%` }} /></div></div>; })}</div></article><article className="panel"><h2>Visit status</h2><div className="status-summary">{['Confirmed', 'Pending', 'Completed'].map((status) => <div key={status}><span className={`status-badge ${status.toLowerCase()}`}>{status}</span><strong>{appointments.filter((item) => item.status === status).length}</strong></div>)}</div></article><article className="panel span-two"><div className="panel-header"><div><h2>Most requested services</h2><p>Visit reasons from current schedule</p></div></div><div className="service-ranking">{['Follow-up', 'General consultation', 'Respiratory consult', 'Chronic care review'].map((service, index) => <div key={service}><strong>{index + 1}</strong><span>{service}</span><div><i style={{ width: `${92 - index * 18}%` }} /></div><b>{12 - index * 2}</b></div>)}</div></article></section>
    </>
  );
}
