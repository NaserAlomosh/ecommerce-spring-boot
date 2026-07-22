import type { ReactNode } from 'react';
import { Sidebar } from '@/components/layout/sidebar';
import { asLocale } from '@/lib/i18n/routing';

type LayoutProps = {
  children: ReactNode;
  params: Promise<{ locale: string }>;
};

export default async function AdminLayout({ children, params }: LayoutProps) {
  const { locale } = await params;
  return (
    <div className="shell">
      <Sidebar locale={asLocale(locale)} mode="admin" />
      <main className="main">{children}</main>
    </div>
  );
}
