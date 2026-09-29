import { Search, UserPlus } from 'lucide-react';
import { PageHeader } from '../../components/PageHeader';
import { useApp } from '../../context/AppContext';

export default function Users() {
  const { users } = useApp();
  const displayed = users.length ? users : [
    { id: 'u-doctor', name: 'Dr. Lina Haddad', email: 'doctor@mediconnect.ai', role: 'DOCTOR' },
    { id: 'u-patient', name: 'Omar Nasser', email: 'patient@mediconnect.ai', role: 'PATIENT' },
    { id: 'u-admin', name: 'Clinic Admin', email: 'admin@mediconnect.ai', role: 'ADMIN' }
  ];
  return (
    <>
      <PageHeader title="User management" description="Review accounts, roles, and access status across the platform." action={<button className="primary-button" type="button"><UserPlus size={17} /> Invite user</button>} />
      <article className="panel"><div className="table-toolbar"><label className="search-field"><Search size={17} /><input placeholder="Search by name or email" /></label><div className="filter-tabs compact"><button className="active" type="button">All users</button><button type="button">Doctors</button><button type="button">Patients</button><button type="button">Admins</button></div></div><div className="responsive-table"><table><thead><tr><th>User</th><th>Role</th><th>Status</th><th>Last active</th><th aria-label="Actions" /></tr></thead><tbody>{displayed.map((account) => <tr key={account.id}><td><div className="user-cell"><span className="patient-avatar soft">{account.name.split(' ').map((part) => part[0]).join('').slice(0, 2)}</span><span><strong>{account.name}</strong><small>{account.email}</small></span></div></td><td><span className={`role-badge ${account.role.toLowerCase()}`}>{account.role}</span></td><td><span className="active-state"><i /> Active</span></td><td>Today</td><td><button className="text-button" type="button">Manage</button></td></tr>)}</tbody></table></div></article>
    </>
  );
}
