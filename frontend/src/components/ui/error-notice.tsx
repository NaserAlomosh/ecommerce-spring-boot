import type { ApiErrorState } from '@/lib/api/errors';

export function ErrorNotice({ error }: { error: ApiErrorState }) {
  return (
    <div className="error-notice" role="alert">
      <strong>{error.title}</strong>
      <span>{error.message}</span>
    </div>
  );
}
