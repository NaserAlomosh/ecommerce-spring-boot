import type { ReactNode } from 'react';
import { RoleGate } from '@/components/auth/role-gate';
import { Sidebar } from '@/components/layout/sidebar';
import { asLocale } from '@/lib/i18n/routing';

type LayoutProps = {
  children: ReactNode;
  params: Promise<{ locale: string }>;
};

export default async function DeliveryLayout({ children, params }: LayoutProps) {
  const { locale: rawLocale } = await params;
  const locale = asLocale(rawLocale);

  return (
    <RoleGate locale={locale} allow={['DELIVERY']}>
      <div className="shell">
        <Sidebar locale={locale} mode="delivery" />
        <main className="main">{children}</main>
      </div>
    </RoleGate>
  );
}
