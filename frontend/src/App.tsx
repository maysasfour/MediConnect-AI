import { useEffect, useState } from 'react';
import { Loading } from './components/Loading';
import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';
import { AppProvider, useApp } from './context/AppContext';
import { useHashRoute } from './hooks/useHashRoute';
import AdminDashboard from './pages/admin/AdminDashboard';
import Analytics from './pages/admin/Analytics';
import AuditLogs from './pages/admin/AuditLogs';
import Users from './pages/admin/Users';
import ForgotPassword from './pages/auth/ForgotPassword';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import Consultation from './pages/doctor/Consultation';
import DoctorDashboard from './pages/doctor/DoctorDashboard';
import PatientProfile from './pages/doctor/PatientProfile';
import PrescriptionBuilder from './pages/doctor/PrescriptionBuilder';
import AIAssistant from './pages/patient/AIAssistant';
import Appointments from './pages/patient/Appointments';
import BookAppointment from './pages/patient/BookAppointment';
import MedicalHistory from './pages/patient/MedicalHistory';
import PatientDashboard from './pages/patient/PatientDashboard';
import PrescriptionHistory from './pages/patient/PrescriptionHistory';

const pageMetadata: Record<string, { title: string; subtitle: string }> = {
  '/doctor': { title: 'Clinical dashboard', subtitle: 'Your care team workspace' },
  '/doctor/patients': { title: 'Patients', subtitle: 'Clinical profiles and history' },
  '/doctor/consultation': { title: 'Consultation', subtitle: 'Document an active encounter' },
  '/doctor/prescriptions': { title: 'Prescriptions', subtitle: 'Medication plan builder' },
  '/patient': { title: 'My health', subtitle: 'Your personal care portal' },
  '/patient/appointments': { title: 'Appointments', subtitle: 'Manage your clinic visits' },
  '/patient/book': { title: 'Book a visit', subtitle: 'Request a convenient appointment' },
  '/patient/history': { title: 'Medical history', subtitle: 'Your shared clinical record' },
  '/patient/prescriptions': { title: 'Prescriptions', subtitle: 'Medication history and instructions' },
  '/patient/assistant': { title: 'Health assistant', subtitle: 'Prepare for your clinician conversation' },
  '/admin': { title: 'Administration', subtitle: 'Platform operations and oversight' },
  '/admin/users': { title: 'Users', subtitle: 'Identity and access management' },
  '/admin/analytics': { title: 'Analytics', subtitle: 'Clinic performance insights' },
  '/admin/audit': { title: 'Audit logs', subtitle: 'Security and access history' }
};

export default function App() {
  return <AppProvider><Portal /></AppProvider>;
}

function Portal() {
  const { user, apiStatus, loading, toast, signOut } = useApp();
  const { route, navigate } = useHashRoute('/login');
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    if (!user && !['/login', '/register', '/forgot-password'].includes(route)) navigate('/login');
    if (user && ['/login', '/register', '/forgot-password', '/'].includes(route)) navigate(`/${user.role.toLowerCase()}`);
    if (user && route.startsWith('/') && !route.startsWith(`/${user.role.toLowerCase()}`) && !['/login', '/register', '/forgot-password'].includes(route)) {
      navigate(`/${user.role.toLowerCase()}`);
    }
  }, [user, route]);

  if (!user) {
    if (route === '/register') return <Register />;
    if (route === '/forgot-password') return <ForgotPassword />;
    return <Login />;
  }

  const metadata = pageMetadata[route] ?? pageMetadata[`/${user.role.toLowerCase()}`];

  function go(nextRoute: string) {
    navigate(nextRoute);
    setMenuOpen(false);
  }

  return (
    <main className={menuOpen ? 'app-shell menu-open' : 'app-shell'}>
      <div className="mobile-backdrop" onClick={() => setMenuOpen(false)} />
      <Sidebar user={user} route={route} onNavigate={go} onSignOut={signOut} />
      <section className="workspace">
        <Navbar user={user} title={metadata.title} subtitle={metadata.subtitle} apiStatus={apiStatus} onMenu={() => setMenuOpen(true)} />
        <div className="page-content">{loading && <Loading label="Syncing workspace" />}{renderPage(route, user.role)}</div>
      </section>
      {toast && <div className="toast" role="status">{toast}</div>}
    </main>
  );
}

function renderPage(route: string, role: string) {
  if (role === 'DOCTOR') {
    if (route === '/doctor/patients') return <PatientProfile />;
    if (route === '/doctor/consultation') return <Consultation />;
    if (route === '/doctor/prescriptions') return <PrescriptionBuilder />;
    return <DoctorDashboard />;
  }
  if (role === 'ADMIN') {
    if (route === '/admin/users') return <Users />;
    if (route === '/admin/analytics') return <Analytics />;
    if (route === '/admin/audit') return <AuditLogs />;
    return <AdminDashboard />;
  }
  if (route === '/patient/appointments') return <Appointments />;
  if (route === '/patient/book') return <BookAppointment />;
  if (route === '/patient/history') return <MedicalHistory />;
  if (route === '/patient/prescriptions') return <PrescriptionHistory />;
  if (route === '/patient/assistant') return <AIAssistant />;
  return <PatientDashboard />;
}
