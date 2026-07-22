import { ResourcePage } from '@/features/admin/resource-page';
import { endpoints } from '@/lib/api/endpoints';

export default function WishlistPage() {
  return <ResourcePage title="Customer Wishlist" url={endpoints.wishlist} actions />;
}
