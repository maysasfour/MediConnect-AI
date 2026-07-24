import { describe, expect, it } from 'vitest';
import {
  formatJod,
  isValidJordanianMobile,
  normaliseJordanianMobile,
} from './locale';

describe('Jordanian mobile validation', () => {
  it('accepts valid +962 mobile numbers', () => {
    expect(isValidJordanianMobile('+962791234567')).toBe(true);
    expect(isValidJordanianMobile('+962781234567')).toBe(true);
    expect(isValidJordanianMobile('+962771234567')).toBe(true);
  });

  it('rejects invalid numbers', () => {
    expect(isValidJordanianMobile('0791234567')).toBe(false); // not E.164
    expect(isValidJordanianMobile('+9626123456')).toBe(false); // landline lead
    expect(isValidJordanianMobile('+96279123456')).toBe(false); // too short
    expect(isValidJordanianMobile('+1234567890')).toBe(false); // wrong country
  });

  it('normalises common local formats to +962 E.164', () => {
    expect(normaliseJordanianMobile('0791234567')).toBe('+962791234567');
    expect(normaliseJordanianMobile('962791234567')).toBe('+962791234567');
    expect(normaliseJordanianMobile('00962791234567')).toBe('+962791234567');
    expect(normaliseJordanianMobile('079-123 4567')).toBe('+962791234567');
    expect(normaliseJordanianMobile('not a number')).toBeNull();
  });
});

describe('JOD formatting', () => {
  it('formats an amount as Jordanian Dinar', () => {
    const formatted = formatJod(25, 'en');
    expect(formatted).toMatch(/JOD|JD/);
    expect(formatted).toContain('25');
  });
});
