import { useApp } from '../context/AppContext';

export function useAuth() {
  const { user, signIn, signOut } = useApp();
  return { user, signIn, signOut };
}
