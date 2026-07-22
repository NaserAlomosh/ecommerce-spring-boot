# Smart Commerce Frontend

Modern bilingual (Arabic/English) Next.js + React + TypeScript frontend for the Spring Boot e-commerce API.

## Run

```bash
cd frontend
npm install
cp .env.example .env.local
npm run dev
```

Open http://localhost:3000 and use `/ar` or `/en`.

## Environment

- `NEXT_PUBLIC_API_BASE_URL`: Spring Boot backend URL, for example `http://localhost:8080`.
- `NEXT_PUBLIC_GOOGLE_CLIENT_ID`: Google OAuth client ID. The implemented social payload is constrained to `provider: 'GOOGLE'` for Gmail/Google auth.

## Architecture

- `src/app`: Next.js route groups for public store, auth, customer account, admin dashboard, and delivery panel.
- `src/features`: feature-level UI and actions.
- `src/lib/api`: API client, endpoint constants, and backend API registry.
- `src/lib/i18n`: Arabic/English dictionaries and locale helpers.
- `src/lib/auth`: browser session handling and role home routing.
- `src/components`: reusable layout and UI components.

## Notes

The public catalog route intentionally does not require login so users can browse products before authentication.
