import Link from 'next/link';
import { t } from '@/lib/i18n/dictionaries';
import { asLocale } from '@/lib/i18n/routing';

type HomeProps = { params: Promise<{ locale: string }> };

export default async function Home({ params }: HomeProps) {
  const { locale: rawLocale } = await params;
  const locale = asLocale(rawLocale);

  return (
    <section className="container grid" style={{ padding: '60px 24px' }}>
      <div className="card" style={{ padding: 42 }}>
        <p className="muted">{t(locale, 'browse')}</p>
        <h1 style={{ fontSize: 56, margin: '8px 0' }}>{t(locale, 'modern')}</h1>
        <Link className="btn" href={`/${locale}/products`}>
          {t(locale, 'products')}
        </Link>
      </div>
    </section>
  );
}
