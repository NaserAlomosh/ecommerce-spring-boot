import Link from 'next/link';
import { t } from '@/lib/i18n/dictionaries';
import { asLocale } from '@/lib/i18n/routing';

type HomeProps = { params: Promise<{ locale: string }> };

export default async function Home({ params }: HomeProps) {
  const { locale: rawLocale } = await params;
  const locale = asLocale(rawLocale);

  return (
    <section className="container hero">
      <div className="hero-copy">
        <p className="eyebrow">Derm beauty store</p>
        <h1>{locale === 'ar' ? 'عناية بالبشرة بتجربة تسوّق فاخرة' : 'Skincare shopping, beautifully refined'}</h1>
        <p className="muted" style={{ maxWidth: 620, fontSize: 18 }}>
          {locale === 'ar'
            ? 'تصفّح منتجات العناية، السيروم، المرطبات، وأساسيات الروتين بدون تسجيل دخول. عند تسجيل الدخول تظهر لك السلة والطلبات حسب دورك.'
            : 'Browse skincare products, serums, moisturizers, and routine essentials without signing in. Role-based areas appear after login.'}
        </p>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap', marginTop: 24 }}>
          <Link className="btn" href={`/${locale}/products`}>{t(locale, 'products')}</Link>
          <Link className="btn secondary" href={`/${locale}/auth/login`}>{t(locale, 'login')}</Link>
        </div>
      </div>

      <div className="card hero-art" aria-hidden="true">
        <span className="hero-drop one" />
        <span className="hero-drop two" />
        <span className="hero-ring" />
        <span className="hero-bottle" />
      </div>
    </section>
  );
}
