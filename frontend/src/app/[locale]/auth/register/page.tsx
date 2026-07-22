'use client';

import { useState } from 'react';
import { ErrorNotice } from '@/components/ui/error-notice';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { register } from '@/features/auth/auth-actions';
import { toApiError, type ApiErrorState } from '@/lib/api/errors';

export default function RegisterPage() {
  const [error, setError] = useState<ApiErrorState | null>(null);
  const [done, setDone] = useState(false);

  return (
    <section className="container" style={{ maxWidth: 620 }}>
      <form className="card grid fade-in" onSubmit={async (event) => {
        event.preventDefault();
        setError(null);
        setDone(false);

        try {
          const form = new FormData(event.currentTarget);
          await register(Object.fromEntries(form) as Record<string, string>);
          setDone(true);
        } catch (err) {
          setError(toApiError(err));
        }
      }}>
        <p className="eyebrow">Join the glow club</p>
        <h1>Register / إنشاء حساب</h1>
        {error && <ErrorNotice error={error} />}
        {done && <div className="error-notice" style={{ color: 'var(--ok)' }}>Account created. Check your email OTP.</div>}
        {['firstName', 'lastName', 'email', 'phoneNumber', 'password'].map((name) => (
          <Input key={name} name={name} type={name === 'password' ? 'password' : 'text'} placeholder={name} />
        ))}
        <Button>Create account</Button>
      </form>
    </section>
  );
}
