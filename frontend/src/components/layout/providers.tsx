'use client';

import { useEffect } from 'react';
import type { PropsWithChildren } from 'react';

export function Providers({ children }: PropsWithChildren) {
  useEffect(() => {
    const theme = localStorage.getItem('theme');
    if (theme === 'dark') document.documentElement.classList.add('dark');
  }, []);

  return <>{children}</>;
}
