'use client';

import { useCallback, useEffect, useState } from 'react';
import { ErrorNotice } from '@/components/ui/error-notice';
import { Button } from '@/components/ui/button';
import { deleteData, getData, patchData } from '@/lib/api/client';
import { toApiError, type ApiErrorState } from '@/lib/api/errors';

type ResourcePageProps = {
  title: string;
  url: string;
  actions?: boolean;
};

function getRows(data: unknown) {
  if (Array.isArray((data as { content?: unknown[] })?.content)) return (data as { content: unknown[] }).content;
  if (Array.isArray(data)) return data;
  return [];
}

function rowKey(row: unknown, index: number) {
  const record = row as { id?: number; orderNumber?: string };
  return record.orderNumber ?? record.id ?? index;
}

export function ResourcePage({ title, url, actions = false }: ResourcePageProps) {
  const [data, setData] = useState<unknown>(null);
  const [error, setError] = useState<ApiErrorState | null>(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      setData(await getData(url));
    } catch (err) {
      setError(toApiError(err));
    } finally {
      setLoading(false);
    }
  }, [url]);

  useEffect(() => {
    void load();
  }, [load]);

  const rows = getRows(data);

  return (
    <section className="grid fade-in">
      <div className="section-heading">
        <p className="eyebrow">Smart operations</p>
        <h1>{title}</h1>
        <Button className="secondary" onClick={() => void load()}>Refresh</Button>
      </div>

      {error && <ErrorNotice error={error} />}
      {loading && <div className="card shimmer">Loading secure data...</div>}

      {!loading && !error && (
        <div className="card table-card">
          {rows.length > 0 ? (
            <table className="data-table">
              <tbody>
                {rows.slice(0, 50).map((row, index) => (
                  <tr key={rowKey(row, index)}>
                    <td><pre>{JSON.stringify(row, null, 2)}</pre></td>
                    {actions && (
                      <td className="row-actions">
                        <Button className="secondary" onClick={() => void patchData(`${url}/${rowKey(row, index)}/status`, {}).then(load).catch((err) => setError(toApiError(err)))}>Update</Button>
                        <Button className="secondary" onClick={() => void deleteData(`${url}/${rowKey(row, index)}`).then(load).catch((err) => setError(toApiError(err)))}>Delete</Button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <pre>{data ? JSON.stringify(data, null, 2) : 'No data yet.'}</pre>
          )}
        </div>
      )}
    </section>
  );
}
