import { Activity, BarChart3, Bot, CalendarDays, ClipboardList, HeartPulse, LogOut, Pill, ShieldCheck, Stethoscope, UserCog, Users, type LucideIcon } from 'lucide-react';
import type { UserProfile } from '../types/user';

type NavItem = { route: string; label: string; icon: LucideIcon };

const navigation: Record<string, NavItem[]> = {
  DOCTOR: [
    { route: '/doctor', label: 'Dashboard', icon: Activity },
    { route: '/doctor/patients', label: 'Patients', icon: Users },
    { route: '/doctor/consultation', label: 'Consultation', icon: Stethoscope },
    { route: '/doctor/prescriptions', label: 'Prescribe', icon: Pill }
  ],
  PATIENT: [
    { route: '/patient', label: 'My health', icon: Activity },
    { route: '/patient/appointments', label: 'Appointments', icon: CalendarDays },
    { route: '/patient/history', label: 'Medical history', icon: ClipboardList },
    { route: '/patient/prescriptions', label: 'Prescriptions', icon: Pill },
    { route: '/patient/assistant', label: 'AI assistant', icon: Bot }
  ],
  ADMIN: [
    { route: '/admin', label: 'Overview', icon: Activity },
    { route: '/admin/users', label: 'Users', icon: UserCog },
    { route: '/admin/analytics', label: 'Analytics', icon: BarChart3 },
    { route: '/admin/audit', label: 'Audit logs', icon: ShieldCheck }
  ]
};

export function Sidebar({ user, route, onNavigate, onSignOut }: { user: UserProfile; route: string; onNavigate: (route: string) => void; onSignOut: () => void }) {
  const items = navigation[user.role] ?? navigation.PATIENT;
  return (
    <aside className="sidebar">
      <div className="brand">
        <span className="brand-mark"><HeartPulse /></span>
        <div><span>MediConnect</span><small>Connected care</small></div>
      </div>
      <nav aria-label="Main navigation">
        {items.map((item) => {
          const Icon = item.icon;
          const active = route === item.route || (item.route !== `/${user.role.toLowerCase()}` && route.startsWith(item.route));
          return (
            <button className={active ? 'nav-link active' : 'nav-link'} key={item.route} onClick={() => onNavigate(item.route)} type="button">
              <Icon size={19} /><span>{item.label}</span>
            </button>
          );
        })}
      </nav>
      <button className="nav-link sign-out" onClick={onSignOut} type="button"><LogOut size={19} /><span>Sign out</span></button>
    </aside>
  );
}
