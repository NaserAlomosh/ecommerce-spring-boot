import type { ReactNode } from 'react';
import './globals.css';
import { Providers } from '@/components/layout/providers';

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html suppressHydrationWarning>
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
