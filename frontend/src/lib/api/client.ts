import { sessionStore } from '@/lib/auth/session';

const apiBaseUrl = () => process.env.NEXT_PUBLIC_API_BASE_URL ?? 'http://localhost:8080';

function toQuery(params?: Record<string, unknown>) {
  if (!params) return '';
  const query = new URLSearchParams();

  for (const [key, value] of Object.entries(params)) {
    if (value === undefined || value === null) continue;
    if (Array.isArray(value)) {
      value.forEach((item) => query.append(key, String(item)));
      continue;
    }
    query.set(key, String(value));
  }

  const rendered = query.toString();
  return rendered ? `?${rendered}` : '';
}

async function request<T>(url: string, init: RequestInit = {}) {
  const token = sessionStore.token();
  const headers = new Headers(init.headers);

  if (!headers.has('Content-Type') && !(init.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  if (token) headers.set('Authorization', `Bearer ${token}`);

  const response = await fetch(`${apiBaseUrl()}${url}`, { ...init, headers });

  if (response.status === 401) sessionStore.clear();
  if (!response.ok) throw new Error(`Request failed: ${response.status}`);

  const json = await response.json();
  return json.data as T;
}

export const api = {
  get: <T>(url: string, params?: Record<string, unknown>) => request<T>(`${url}${toQuery(params)}`),
  post: <T>(url: string, body?: unknown) => request<T>(url, { method: 'POST', body: body instanceof FormData ? body : JSON.stringify(body ?? {}) }),
  patch: <T>(url: string, body?: unknown) => request<T>(url, { method: 'PATCH', body: JSON.stringify(body ?? {}) }),
  put: <T>(url: string, body?: unknown) => request<T>(url, { method: 'PUT', body: JSON.stringify(body ?? {}) }),
  delete: <T>(url: string) => request<T>(url, { method: 'DELETE' }),
};

export const getData = api.get;
export const postData = api.post;
export const patchData = api.patch;
export const putData = api.put;
export const deleteData = api.delete;
