'use client';

import { postData } from '@/lib/api/client';
import { endpoints } from '@/lib/api/endpoints';
import { roleHome, sessionStore } from '@/lib/auth/session';
import type { Locale, Tokens } from '@/types/api';

function saveTokensAndRedirect(tokens: Tokens, locale: Locale) {
  sessionStore.set({ tokens });
  const role = sessionStore.get()?.role;
  location.href = roleHome(role, locale);
}

export async function login(email: string, password: string, locale: Locale = 'ar') {
  const tokens = await postData<Tokens>(`${endpoints.auth}/login`, { email, password });
  saveTokensAndRedirect(tokens, locale);
}

export async function register(body: Record<string, string>) {
  await postData(`${endpoints.auth}/register`, body);
}

export async function googleLogin(identityToken: string, locale: Locale = 'ar') {
  const tokens = await postData<Tokens>(`${endpoints.auth}/social-login`, { provider: 'GOOGLE', identityToken });
  saveTokensAndRedirect(tokens, locale);
}

export function logout(locale: Locale = 'ar') {
  sessionStore.clear();
  location.href = `/${locale}`;
}
