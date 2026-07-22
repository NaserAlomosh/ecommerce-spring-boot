import { ResourcePage } from '@/features/admin/resource-page';
import { endpoints } from '@/lib/api/endpoints';

export default function CartPage() {
  return <ResourcePage title="Customer Cart" url={endpoints.cart} actions />;
}
