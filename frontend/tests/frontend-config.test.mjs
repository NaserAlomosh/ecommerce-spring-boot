import assert from 'node:assert/strict';
import { test } from 'node:test';
import fs from 'node:fs';

test('frontend defines bilingual locales and google social login payload', () => {
  const dict = fs.readFileSync(new URL('../src/lib/i18n/dictionaries.ts', import.meta.url), 'utf8');
  const auth = fs.readFileSync(new URL('../src/features/auth/auth-actions.ts', import.meta.url), 'utf8');
  assert.match(dict, /en:/);
  assert.match(dict, /ar:/);
  assert.match(auth, /provider:'GOOGLE'/);
});

test('API endpoint map covers public, customer, admin, and delivery areas', () => {
  const endpoints = fs.readFileSync(new URL('../src/lib/api/endpoints.ts', import.meta.url), 'utf8');
  for (const token of ['/api/v1/products','/api/v1/customer/cart','/api/v1/admin/dashboard','/api/v1/delivery/orders']) {
    assert.ok(endpoints.includes(token), `${token} missing`);
  }
});


test('API registry documents broad backend coverage', () => {
  const registry = fs.readFileSync(new URL('../src/lib/api/registry.ts', import.meta.url), 'utf8');
  for (const token of ['social-login GOOGLE/Gmail only','/api/v1/admin/reports/sales/summary','/api/v1/customer/addresses','/api/v1/delivery/orders/{orderNumber}/status']) {
    assert.ok(registry.includes(token), `${token} missing`);
  }
});


test('package is named and pins Next to the build-tested major', () => {
  const pkg = JSON.parse(fs.readFileSync(new URL('../package.json', import.meta.url), 'utf8'));
  assert.equal(pkg.name, 'smart-commerce-frontend');
  assert.equal(pkg.dependencies.next, '16.2.11');
  assert.ok(pkg.devDependencies.typescript);
});
