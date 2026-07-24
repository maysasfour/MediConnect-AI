import createCache from '@emotion/cache';
import { prefixer } from 'stylis';
import rtlPlugin from 'stylis-plugin-rtl';

/** Emotion cache that flips physical CSS properties for RTL when needed. */
export function createEmotionCache(direction: 'ltr' | 'rtl') {
  return createCache({
    key: direction === 'rtl' ? 'murtl' : 'mu',
    stylisPlugins: direction === 'rtl' ? [prefixer, rtlPlugin] : [prefixer],
  });
}
