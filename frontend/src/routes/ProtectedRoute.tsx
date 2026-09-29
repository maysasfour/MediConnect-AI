import type { ReactNode } from 'react';
import type { UserProfile, UserRole } from '../types/user';

export function ProtectedRoute({ children, user, roles }: { children: ReactNode; user: UserProfile | null; roles?: UserRole[] }) {
  if (!user) {
    window.location.hash = '#/login';
    return null;
  }

  if (roles && !roles.includes(user.role as UserRole)) {
    window.location.hash = `#/${user.role.toLowerCase()}`;
    return null;
  }

  return <>{children}</>;
}
