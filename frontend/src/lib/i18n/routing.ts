import type { Locale } from '@/types/api'; export const locales:Locale[]=['ar','en']; export function asLocale(v:string):Locale{return v==='en'?'en':'ar'}
