import type { ReactNode } from 'react';
import { RoleGate } from '@/components/auth/role-gate';
import { Sidebar } from '@/components/layout/sidebar';
import { asLocale } from '@/lib/i18n/routing';

type LayoutProps = {
  children: ReactNode;
  params: Promise<{ locale: string }>;
};

export default async function AdminLayout({ children, params }: LayoutProps) {
  const { locale: rawLocale } = await params;
  const locale = asLocale(rawLocale);

  return (
    <RoleGate locale={locale} allow={['ADMIN', 'SUB_ADMIN']}>
      <div className="shell">
        <Sidebar locale={locale} mode="admin" />
        <main className="main">{children}</main>
      </div>
    </RoleGate>
  );
}
