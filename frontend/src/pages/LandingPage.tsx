import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { Link as RouterLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

export function LandingPage() {
  const { t } = useTranslation();
  return (
    <Container maxWidth="md" sx={{ py: 6 }}>
      <Box component="section" aria-labelledby="hero-title">
        <Typography id="hero-title" variant="h3" component="h1" gutterBottom>
          {t('landing.heroTitle')}
        </Typography>
        <Typography variant="h6" component="p" color="text.secondary" paragraph>
          {t('landing.heroSubtitle')}
        </Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 3 }}>
          <Button variant="contained" component={RouterLink} to="/login">
            {t('landing.cta')}
          </Button>
          <Button variant="outlined" component={RouterLink} to="/assistant">
            {t('landing.aiCta')}
          </Button>
        </Stack>
      </Box>
    </Container>
  );
}
