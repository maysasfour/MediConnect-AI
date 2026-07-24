/**
 * Jordan-specific formatting and validation helpers. Kept pure so they can be
 * unit-tested and reused across forms and display components.
 */

const AMMAN_TZ = 'Asia/Amman';

/**
 * Validates a Jordanian mobile number in +962 international format.
 * Accepted: +9627XXXXXXXX (7,8,9 lead) — i.e. +962 followed by a 9-digit
 * subscriber number starting with 7.
 */
export function isValidJordanianMobile(value: string): boolean {
  return /^\+9627[789]\d{7}$/.test(value.trim());
}

/**
 * Normalises common local inputs (07XXXXXXXX or 9627...) to +962 E.164, or
 * returns null if it cannot be interpreted as a Jordanian mobile.
 */
export function normaliseJordanianMobile(value: string): string | null {
  const digits = value.replace(/[\s-]/g, '');
  let candidate: string | null = null;
  if (/^\+9627\d{8}$/.test(digits)) candidate = digits;
  else if (/^009627\d{8}$/.test(digits)) candidate = '+' + digits.slice(2);
  else if (/^9627\d{8}$/.test(digits)) candidate = '+' + digits;
  else if (/^07\d{8}$/.test(digits)) candidate = '+962' + digits.slice(1);
  return candidate && isValidJordanianMobile(candidate) ? candidate : null;
}

/** Formats an amount in Jordanian Dinar (3 decimal places, JOD). */
export function formatJod(amount: number, locale: string): string {
  return new Intl.NumberFormat(locale === 'ar' ? 'ar-JO' : 'en-JO', {
    style: 'currency',
    currency: 'JOD',
  }).format(amount);
}

/** Formats a date/time in the Amman timezone using the active locale. */
export function formatDateTime(value: Date | string, locale: string): string {
  const date = typeof value === 'string' ? new Date(value) : value;
  return new Intl.DateTimeFormat(locale === 'ar' ? 'ar-JO' : 'en-JO', {
    dateStyle: 'medium',
    timeStyle: 'short',
    timeZone: AMMAN_TZ,
  }).format(date);
}
