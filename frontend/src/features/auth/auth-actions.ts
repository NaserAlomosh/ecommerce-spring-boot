'use client'; import { postData } from '@/lib/api/client'; import { endpoints } from '@/lib/api/endpoints'; import { sessionStore, roleHome } from '@/lib/auth/session'; import type { Tokens } from '@/types/api';
export async function login(email:string,password:string){const tokens=await postData<Tokens>(`${endpoints.auth}/login`,{email,password}); sessionStore.set({tokens}); location.href=roleHome()}
export async function register(body:Record<string,string>){await postData(`${endpoints.auth}/register`,body)}
export async function googleLogin(identityToken:string){const tokens=await postData<Tokens>(`${endpoints.auth}/social-login`,{provider:'GOOGLE',identityToken}); sessionStore.set({tokens}); location.href=roleHome()}
