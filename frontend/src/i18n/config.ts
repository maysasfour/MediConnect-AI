import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './en.json';
import ar from './ar.json';

export const SUPPORTED_LANGUAGES = ['en', 'ar'] as const;
export type SupportedLanguage = (typeof SUPPORTED_LANGUAGES)[number];

export function isRtl(language: string): boolean {
  return language === 'ar';
}

/**
 * Keep the document direction and lang in sync with the active language so RTL
 * layout and screen readers behave correctly.
 */
export function applyDocumentDirection(language: string): void {
  if (typeof document === 'undefined') return;
  document.documentElement.lang = language;
  document.documentElement.dir = isRtl(language) ? 'rtl' : 'ltr';
}

const stored =
  typeof localStorage !== 'undefined' ? localStorage.getItem('lang') : null;
const initial: SupportedLanguage =
  stored === 'ar' || stored === 'en' ? stored : 'en';

void i18n.use(initReactI18next).init({
  resources: {
    en: { translation: en },
    ar: { translation: ar },
  },
  lng: initial,
  fallbackLng: 'en',
  interpolation: { escapeValue: false },
});

applyDocumentDirection(initial);

i18n.on('languageChanged', (lng) => {
  applyDocumentDirection(lng);
  if (typeof localStorage !== 'undefined') localStorage.setItem('lang', lng);
});

export default i18n;
