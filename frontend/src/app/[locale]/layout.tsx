import type { ReactNode } from 'react';
import { asLocale } from '@/lib/i18n/routing';
import { Nav } from '@/components/layout/nav';

type LocaleLayoutProps = {
  children: ReactNode;
  params: Promise<{ locale: string }>;
};

export default async function LocaleLayout({ children, params }: LocaleLayoutProps) {
  const { locale: rawLocale } = await params;
  const locale = asLocale(rawLocale);

  return (
    <main className={locale === 'ar' ? 'rtl' : 'ltr'}>
      <Nav locale={locale} />
      {children}
    </main>
  );
}
