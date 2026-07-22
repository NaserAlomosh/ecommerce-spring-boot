'use client';

import { useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { isAdminRole, isCustomerRole, isDeliveryRole, sessionStore } from '@/lib/auth/session';
import type { Locale, Role } from '@/types/api';

type RoleGateProps = {
  locale: Locale;
  allow: Role[];
  children: ReactNode;
};

function allowed(role: Role | undefined, roles: Role[]) {
  if (!role) return false;
  if (roles.includes(role)) return true;
  if (roles.includes('ADMIN') && isAdminRole(role)) return true;
  if (roles.includes('CUSTOMER') && isCustomerRole(role)) return true;
  if (roles.includes('DELIVERY') && isDeliveryRole(role)) return true;
  return false;
}

export function RoleGate({ locale, allow, children }: RoleGateProps) {
  const router = useRouter();
  const [ready, setReady] = useState(false);
  const [role, setRole] = useState<Role | undefined>();
  const isAllowed = useMemo(() => allowed(role, allow), [allow, role]);

  useEffect(() => {
    const session = sessionStore.get();
    setRole(session?.role);
    setReady(true);

    if (!session?.tokens.accessToken) router.replace(`/${locale}/auth/login`);
  }, [locale, router]);

  if (!ready) return <div className="card shimmer">Checking secure access...</div>;

  if (!isAllowed) {
    return (
      <section className="card access-denied">
        <p className="eyebrow">403</p>
        <h1>{locale === 'ar' ? 'لا تملك صلاحية الوصول' : 'Access denied'}</h1>
        <p className="muted">
          {locale === 'ar'
            ? 'هذه الصفحة تظهر فقط للدور المناسب بعد تسجيل الدخول.'
            : 'This page is only visible to the matching logged-in role.'}
        </p>
        <Link className="btn" href={`/${locale}/auth/login`}>{locale === 'ar' ? 'تسجيل الدخول' : 'Login'}</Link>
      </section>
    );
  }

  return <>{children}</>;
}
