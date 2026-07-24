import Button from '@mui/material/Button';
import { useTranslation } from 'react-i18next';

/** Toggles between English and Arabic; document direction follows via i18n config. */
export function LanguageSwitcher() {
  const { i18n, t } = useTranslation();
  const next = i18n.language === 'ar' ? 'en' : 'ar';
  return (
    <Button
      color="inherit"
      onClick={() => void i18n.changeLanguage(next)}
      aria-label={next === 'ar' ? t('language.switchToArabic') : t('language.switchToEnglish')}
    >
      {next === 'ar' ? t('language.switchToArabic') : t('language.switchToEnglish')}
    </Button>
  );
}
