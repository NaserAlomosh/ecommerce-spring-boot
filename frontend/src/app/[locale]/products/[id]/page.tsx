import { productService } from '@/features/products/product-service';
import { asLocale } from '@/lib/i18n/routing';

type ProductDetailsProps = { params: Promise<{ locale: string; id: string }> };

export default async function ProductDetails({ params }: ProductDetailsProps) {
  const { locale: rawLocale, id } = await params;
  const locale = asLocale(rawLocale);
  const product = await productService.get(id);

  return (
    <section className="container grid">
      <article className="card">
        <div style={{ height: 320, borderRadius: 22, background: 'linear-gradient(135deg,var(--brand),var(--brand2))' }} />
        <h1>{locale === 'ar' ? product.nameAr : product.nameEn}</h1>
        <p className="muted">{product.sku}</p>
        <h2>{product.effectivePrice ?? product.price} {product.currency}</h2>
        <p>{product.inStock ? 'In stock' : 'Out of stock'} · ★ {product.averageRating ?? 0} ({product.reviewsCount ?? 0})</p>
        <button className="btn">Add to cart / أضف للسلة</button>
      </article>
    </section>
  );
}
