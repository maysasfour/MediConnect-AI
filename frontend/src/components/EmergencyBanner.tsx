import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';

/**
 * Interrupts the assistant when an emergency red flag is detected. It uses an
 * assertive live region so screen readers announce it immediately, and conveys
 * urgency with an icon and text — never colour alone (WCAG 1.4.1).
 */
export function EmergencyBanner() {
  const { t } = useTranslation();
  return (
    <Alert
      severity="error"
      variant="filled"
      role="alert"
      aria-live="assertive"
      data-testid="emergency-banner"
    >
      <AlertTitle>{t('emergency.title')}</AlertTitle>
      <Stack spacing={1}>
        <Typography>{t('emergency.body')}</Typography>
        <Button
          component="a"
          href="tel:911"
          variant="contained"
          color="inherit"
          sx={{ alignSelf: 'flex-start', color: '#b00020', bgcolor: '#fff' }}
        >
          {t('emergency.call')}
        </Button>
      </Stack>
    </Alert>
  );
}
