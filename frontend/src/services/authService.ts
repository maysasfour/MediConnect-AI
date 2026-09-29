import { apiRequest } from '../api/axios';
import { endpoints } from '../api/endpoints';
import type { UserProfile } from '../types/user';

export type AuthResponse = {
  token: string;
  user: UserProfile;
};

type VersionedAuthResponse = {
  accessToken: string;
  refreshToken: string;
  user: {
    id: string;
    fullName: string;
    email: string;
    role: string;
  };
};

export async function login(email: string, password: string): Promise<AuthResponse> {
  const response = await apiRequest<VersionedAuthResponse>(endpoints.versionedLogin, {
    method: 'POST',
    body: JSON.stringify({ email, password })
  });

  return {
    token: response.accessToken,
    user: {
      id: response.user.id,
      name: response.user.fullName,
      email: response.user.email,
      role: response.user.role
    }
  };
}

export async function register(fullName: string, email: string, password: string): Promise<AuthResponse> {
  const response = await apiRequest<VersionedAuthResponse>(endpoints.register, {
    method: 'POST',
    body: JSON.stringify({ fullName, email, password, role: 'PATIENT' })
  });

  return {
    token: response.accessToken,
    user: {
      id: response.user.id,
      name: response.user.fullName,
      email: response.user.email,
      role: response.user.role
    }
  };
}
