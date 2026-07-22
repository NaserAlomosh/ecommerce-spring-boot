'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useEffect, useMemo, useState } from 'react';
import { logout } from '@/features/auth/auth-actions';
import { isAdminRole, isCustomerRole, isDeliveryRole, sessionStore } from '@/lib/auth/session';
import { t } from '@/lib/i18n/dictionaries';
import type { Locale, Role } from '@/types/api';

type NavItem = { href: string; label: string; show: boolean };

function toggleTheme() {
  document.documentElement.classList.toggle('dark');
  localStorage.setItem('theme', document.documentElement.classList.contains('dark') ? 'dark' : 'light');
}

export function Nav({ locale }: { locale: Locale }) {
  const path = usePathname();
  const [role, setRole] = useState<Role | undefined>();
  const [loggedIn, setLoggedIn] = useState(false);
  const other = locale === 'ar' ? 'en' : 'ar';
  const otherPath = path.replace(`/${locale}`, `/${other}`);

  useEffect(() => {
    const sync = () => {
      const session = sessionStore.get();
      setRole(session?.role);
      setLoggedIn(Boolean(session?.tokens.accessToken));
    };

    sync();
    window.addEventListener('storage', sync);
    window.addEventListener('smart-session-change', sync);
    return () => {
      window.removeEventListener('storage', sync);
      window.removeEventListener('smart-session-change', sync);
    };
  }, []);

  const items = useMemo<NavItem[]>(() => [
    { href: `/${locale}/products`, label: t(locale, 'products'), show: true },
    { href: `/${locale}/cart`, label: t(locale, 'cart'), show: loggedIn && isCustomerRole(role) },
    { href: `/${locale}/wishlist`, label: t(locale, 'wishlist'), show: loggedIn && isCustomerRole(role) },
    { href: `/${locale}/orders`, label: t(locale, 'orders'), show: loggedIn && isCustomerRole(role) },
    { href: `/${locale}/admin`, label: t(locale, 'admin'), show: loggedIn && isAdminRole(role) },
    { href: `/${locale}/delivery`, label: t(locale, 'delivery'), show: loggedIn && isDeliveryRole(role) },
  ], [locale, loggedIn, role]);

  return (
    <header className="glass-nav">
      <Link href={`/${locale}`} className="brand-mark">
        <span className="brand-orb" />
        {t(locale, 'brand')}
      </Link>

      <nav className="nav-links" aria-label="Primary navigation">
        {items.filter((item) => item.show).map((item) => (
          <Link key={item.href} href={item.href}>{item.label}</Link>
        ))}
      </nav>

      <div className="nav-actions">
        {!loggedIn ? (
          <Link className="btn secondary" href={`/${locale}/auth/login`}>{t(locale, 'login')}</Link>
        ) : (
          <button className="btn secondary" onClick={() => logout(locale)}>{locale === 'ar' ? 'خروج' : 'Logout'}</button>
        )}
        <Link className="btn secondary" href={otherPath}>{other.toUpperCase()}</Link>
        <button className="btn secondary" aria-label="toggle theme" onClick={toggleTheme}>◐</button>
      </div>
    </header>
  );
}
