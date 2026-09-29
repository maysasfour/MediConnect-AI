import type { UserProfile } from '../types/user';
import { Bell, Menu, Search } from 'lucide-react';

export function Navbar({ user, title, subtitle, apiStatus, onMenu }: { user: UserProfile; title: string; subtitle: string; apiStatus: string; onMenu: () => void }) {
  return (
    <header className="topbar">
      <button className="mobile-menu" type="button" onClick={onMenu} aria-label="Open navigation"><Menu /></button>
      <div className="page-heading">
        <p className="eyebrow"><span className={`status-dot ${apiStatus === 'Connected' ? 'online' : ''}`} /> {apiStatus}</p>
        <h1>{title}</h1>
        <p>{subtitle}</p>
      </div>
      <div className="topbar-actions">
        <label className="header-search"><Search size={17} /><input aria-label="Search" placeholder="Search workspace" /></label>
        <button className="icon-button" type="button" aria-label="Notifications"><Bell size={19} /><span /></button>
        <div className="user-pill">
          <span className="avatar">{user.name.split(' ').map((part) => part[0]).slice(0, 2).join('')}</span>
          <div><strong>{user.name}</strong><small>{user.role.toLowerCase()}</small></div>
        </div>
      </div>
    </header>
  );
}
