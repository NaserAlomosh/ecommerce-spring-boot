import type { ButtonHTMLAttributes, PropsWithChildren } from 'react';
export function Button({children,className='',...props}:PropsWithChildren<ButtonHTMLAttributes<HTMLButtonElement>>){return <button className={`btn ${className}`} {...props}>{children}</button>}
