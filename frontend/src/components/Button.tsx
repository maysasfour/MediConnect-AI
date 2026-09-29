import type { ButtonHTMLAttributes, ReactNode } from 'react';

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  children: ReactNode;
  variant?: 'primary' | 'secondary';
};

export function Button({ children, className = '', variant = 'primary', ...props }: ButtonProps) {
  const variantClass = variant === 'primary' ? 'primary-button' : 'secondary-button';
  return (
    <button className={`${variantClass} ${className}`.trim()} type="button" {...props}>
      {children}
    </button>
  );
}
