import type { Role, Tokens, User } from '@/types/api';
const key='smart_shop_session'; export type Session={tokens:Tokens;user?:User;role?:Role};
export const sessionStore={get():Session|null{if(typeof window==='undefined')return null; const raw=localStorage.getItem(key); return raw?JSON.parse(raw) as Session:null},set(s:Session){localStorage.setItem(key,JSON.stringify(s))},clear(){localStorage.removeItem(key)},token(){return this.get()?.tokens.accessToken}};
export const roleHome=(role?:Role)=>role==='ADMIN'||role==='SUB_ADMIN'?'/admin':role==='DELIVERY'?'/delivery':'/account';
