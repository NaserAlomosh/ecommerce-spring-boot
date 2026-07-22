'use client';

import { useParams } from 'next/navigation';
import { useState } from 'react';
import { ErrorNotice } from '@/components/ui/error-notice';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { googleLogin, login } from '@/features/auth/auth-actions';
import { toApiError, type ApiErrorState } from '@/lib/api/errors';
import { asLocale } from '@/lib/i18n/routing';

export default function LoginPage() {
  const params = useParams<{ locale: string }>();
  const locale = asLocale(params.locale);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [googleToken, setGoogleToken] = useState('');
  const [error, setError] = useState<ApiErrorState | null>(null);
  const [loading, setLoading] = useState(false);

  async function submit(action: () => Promise<void>) {
    setLoading(true);
    setError(null);

    try {
      await action();
    } catch (err) {
      setError(toApiError(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="container" style={{ maxWidth: 560 }}>
      <form className="card grid fade-in" onSubmit={(event) => { event.preventDefault(); void submit(() => login(email, password, locale)); }}>
        <p className="eyebrow">Secure beauty account</p>
        <h1>{locale === 'ar' ? 'تسجيل الدخول' : 'Login'}</h1>
        {error && <ErrorNotice error={error} />}
        <Input placeholder="email" value={email} onChange={(event) => setEmail(event.target.value)} />
        <Input placeholder="password" type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
        <Button disabled={loading}>{loading ? '...' : locale === 'ar' ? 'دخول' : 'Login'}</Button>
        <hr style={{ borderColor: 'var(--border)', width: '100%' }} />
        <Input placeholder="Google Gmail identity token" value={googleToken} onChange={(event) => setGoogleToken(event.target.value)} />
        <Button type="button" className="secondary" disabled={loading} onClick={() => void submit(() => googleLogin(googleToken, locale))}>
          {locale === 'ar' ? 'المتابعة عبر Gmail' : 'Continue with Gmail'}
        </Button>
      </form>
    </section>
  );
}
