import Link from 'next/link';
import type { Locale, Product } from '@/types/api';

export function ProductGrid({ products, locale }: { products: Product[]; locale: Locale }) {
  return (
    <div className="grid product-grid">
      {products.map((product, index) => (
        <Link href={`/${locale}/products/${product.id}`} key={product.id} className="card product-card fade-in" style={{ animationDelay: `${index * 55}ms` }}>
          <div className="product-image" />
          <p className="eyebrow">{product.featured ? 'Featured' : product.category?.nameEn ?? 'Skincare'}</p>
          <h3>{locale === 'ar' ? product.nameAr : product.nameEn}</h3>
          <p className="muted">{product.sku}</p>
          <p className="price">{product.effectivePrice ?? product.price} {product.currency}</p>
          <p className="muted">{product.inStock ? '✓ In stock' : 'Out of stock'} · ★ {product.averageRating ?? 0}</p>
        </Link>
      ))}
    </div>
  );
}
