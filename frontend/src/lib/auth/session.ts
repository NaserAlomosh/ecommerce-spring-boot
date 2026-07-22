import type { Role, Tokens, User } from '@/types/api';

const storageKey = 'smart_shop_session';

type JwtPayload = {
  role?: Role;
  userId?: number;
  sub?: string;
  exp?: number;
};

export type Session = {
  tokens: Tokens;
  user?: User;
  role?: Role;
  email?: string;
  userId?: number;
};

function decodeBase64Url(value: string) {
  const normalized = value.replace(/-/g, '+').replace(/_/g, '/');
  const padded = normalized.padEnd(normalized.length + ((4 - (normalized.length % 4)) % 4), '=');
  return atob(padded);
}

export function decodeJwt(token?: string): JwtPayload | null {
  if (!token || typeof window === 'undefined') return null;

  try {
    const [, payload] = token.split('.');
    if (!payload) return null;
    return JSON.parse(decodeBase64Url(payload)) as JwtPayload;
  } catch {
    return null;
  }
}

function enrichSession(session: Session): Session {
  const payload = decodeJwt(session.tokens.accessToken);
  return {
    ...session,
    role: session.role ?? payload?.role,
    email: session.email ?? payload?.sub,
    userId: session.userId ?? payload?.userId,
  };
}

export const sessionStore = {
  get(): Session | null {
    if (typeof window === 'undefined') return null;
    const raw = localStorage.getItem(storageKey);
    if (!raw) return null;

    try {
      return enrichSession(JSON.parse(raw) as Session);
    } catch {
      localStorage.removeItem(storageKey);
      return null;
    }
  },

  set(session: Session) {
    if (typeof window === 'undefined') return;
    localStorage.setItem(storageKey, JSON.stringify(enrichSession(session)));
    window.dispatchEvent(new Event('smart-session-change'));
  },

  clear() {
    if (typeof window === 'undefined') return;
    localStorage.removeItem(storageKey);
    window.dispatchEvent(new Event('smart-session-change'));
  },

  token() {
    return this.get()?.tokens.accessToken;
  },
};

export const isAdminRole = (role?: Role) => role === 'ADMIN' || role === 'SUB_ADMIN';
export const isDeliveryRole = (role?: Role) => role === 'DELIVERY';
export const isCustomerRole = (role?: Role) => role === 'CUSTOMER';

export function roleHome(role: Role | undefined, locale = 'ar') {
  if (isAdminRole(role)) return `/${locale}/admin`;
  if (isDeliveryRole(role)) return `/${locale}/delivery`;
  return `/${locale}/account/profile`;
}
