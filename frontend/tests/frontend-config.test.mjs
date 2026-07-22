import assert from 'node:assert/strict';
import { test } from 'node:test';
import fs from 'node:fs';

function read(path) {
  return fs.readFileSync(new URL(path, import.meta.url), 'utf8');
}

test('frontend defines bilingual skincare branding and google social login payload', () => {
  const dict = read('../src/lib/i18n/dictionaries.ts');
  const auth = read('../src/features/auth/auth-actions.ts');
  assert.match(dict, /Naya Beauty/);
  assert.match(dict, /نايا بيوتي/);
  assert.match(auth, /provider: 'GOOGLE'/);
});

test('API endpoint map covers public, customer, admin, and delivery areas', () => {
  const endpoints = read('../src/lib/api/endpoints.ts');
  for (const token of ['/api/v1/products', '/api/v1/customer/cart', '/api/v1/admin/dashboard', '/api/v1/delivery/orders']) {
    assert.ok(endpoints.includes(token), `${token} missing`);
  }
});

test('API registry documents broad backend coverage', () => {
  const registry = read('../src/lib/api/registry.ts');
  for (const token of ['social-login GOOGLE/Gmail only', '/api/v1/admin/reports/sales/summary', '/api/v1/customer/addresses', '/api/v1/delivery/orders/{orderNumber}/status']) {
    assert.ok(registry.includes(token), `${token} missing`);
  }
});

test('package is named and pins Next to the build-tested major', () => {
  const pkg = JSON.parse(read('../package.json'));
  assert.equal(pkg.name, 'smart-commerce-frontend');
  assert.equal(pkg.dependencies.next, '16.2.11');
  assert.ok(pkg.devDependencies.typescript);
});

test('navigation and layouts are role aware', () => {
  const nav = read('../src/components/layout/nav.tsx');
  const adminLayout = read('../src/app/[locale]/admin/layout.tsx');
  const deliveryLayout = read('../src/app/[locale]/delivery/layout.tsx');
  const accountLayout = read('../src/app/[locale]/account/layout.tsx');
  assert.match(nav, /loggedIn && isCustomerRole\(role\)/);
  assert.match(nav, /loggedIn && isAdminRole\(role\)/);
  assert.match(nav, /loggedIn && isDeliveryRole\(role\)/);
  assert.match(adminLayout, /allow=\{\['ADMIN', 'SUB_ADMIN'\]\}/);
  assert.match(deliveryLayout, /allow=\{\['DELIVERY'\]\}/);
  assert.match(accountLayout, /allow=\{\['CUSTOMER'\]\}/);
});

test('frontend has shared API error handling and animated skincare UI', () => {
  const errors = read('../src/lib/api/errors.ts');
  const resourcePage = read('../src/features/admin/resource-page.tsx');
  const css = read('../src/app/globals.css');
  assert.match(errors, /toApiError/);
  assert.match(resourcePage, /ErrorNotice/);
  assert.match(css, /@keyframes slide-up/);
  assert.match(css, /hero-bottle/);
});
