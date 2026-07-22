import Link from 'next/link';
import type { Locale } from '@/types/api';
import { dict, t } from '@/lib/i18n/dictionaries';

type SidebarMode = 'admin' | 'delivery' | 'customer';
type MenuKey = keyof typeof dict.en;

const labels: Record<string, MenuKey> = {
  admin: 'admin',
  products: 'products',
  categories: 'categories',
  orders: 'orders',
  users: 'users',
  inventory: 'inventory',
  reports: 'reports',
  delivery: 'delivery',
  profile: 'profile',
  cart: 'cart',
  wishlist: 'wishlist',
};

function hrefFor(locale: Locale, mode: SidebarMode, item: string) {
  if (mode === 'admin') return item === 'admin' ? `/${locale}/admin` : `/${locale}/admin/${item}`;
  if (mode === 'delivery') return `/${locale}/delivery`;
  return `/${locale}/account/${item}`;
}

export function Sidebar({ locale, mode }: { locale: Locale; mode: SidebarMode }) {
  const admin = ['admin', 'products', 'categories', 'orders', 'users', 'inventory', 'reports'];
  const customer = ['profile', 'cart', 'wishlist', 'orders'];
  const items = mode === 'delivery' ? ['delivery'] : mode === 'admin' ? admin : customer;

  return (
    <aside className="sidebar">
      <h2>{mode === 'admin' ? t(locale, 'admin') : mode === 'delivery' ? t(locale, 'delivery') : t(locale, 'profile')}</h2>
      <div className="grid">
        {items.map((item) => (
          <Link className="card" key={item} href={hrefFor(locale, mode, item)}>
            {t(locale, labels[item])}
          </Link>
        ))}
      </div>
    </aside>
  );
}
