import { ProductGrid } from '@/features/products/product-grid';
import { productService } from '@/features/products/product-service';
import { asLocale } from '@/lib/i18n/routing';
import type { Product } from '@/types/api';

type ProductsPageProps = {
  params: Promise<{ locale: string }>;
  searchParams: Promise<Record<string, string | string[] | undefined>>;
};

export default async function ProductsPage({ params, searchParams }: ProductsPageProps) {
  const [{ locale: rawLocale }, query] = await Promise.all([params, searchParams]);
  const locale = asLocale(rawLocale);
  let products: Product[] = [];

  try {
    products = (await productService.list(query)).content;
  } catch {
    products = [];
  }

  return (
    <section className="container grid">
      <h1>{locale === 'ar' ? 'المنتجات' : 'Products'}</h1>
      <ProductGrid products={products} locale={locale} />
    </section>
  );
}
