import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import CssBaseline from '@mui/material/CssBaseline';
import { CacheProvider } from '@emotion/react';
import { ThemeProvider } from '@mui/material/styles';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, Route, Routes } from 'react-router-dom';
import { LanguageSwitcher } from './components/LanguageSwitcher';
import { AiAssistantPage } from './pages/AiAssistantPage';
import { LandingPage } from './pages/LandingPage';
import { LoginPage } from './pages/LoginPage';
import { AiDisclaimer } from './components/AiDisclaimer';
import { createEmotionCache } from './theme/rtlCache';
import { buildTheme } from './theme/theme';
import { isRtl } from './i18n/config';
import Container from '@mui/material/Container';

const queryClient = new QueryClient();

export default function App() {
  const { i18n, t } = useTranslation();
  const direction = isRtl(i18n.language) ? 'rtl' : 'ltr';
  const cache = useMemo(() => createEmotionCache(direction), [direction]);
  const theme = useMemo(() => buildTheme(direction), [direction]);

  return (
    <CacheProvider value={cache}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <QueryClientProvider client={queryClient}>
          <Box dir={direction}>
            <AppBar position="static">
              <Toolbar>
                <Typography variant="h6" component={RouterLink} to="/"
                  sx={{ flexGrow: 1, color: 'inherit', textDecoration: 'none' }}>
                  {t('app.name')}
                </Typography>
                <Button color="inherit" component={RouterLink} to="/assistant">
                  {t('nav.assistant')}
                </Button>
                <Button color="inherit" component={RouterLink} to="/login">
                  {t('nav.login')}
                </Button>
                <LanguageSwitcher />
              </Toolbar>
            </AppBar>
            <main>
              <Routes>
                <Route path="/" element={<LandingPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/assistant" element={<AiAssistantPage />} />
                <Route
                  path="/ai-disclaimer"
                  element={
                    <Container maxWidth="md" sx={{ py: 4 }}>
                      <AiDisclaimer />
                    </Container>
                  }
                />
              </Routes>
            </main>
          </Box>
        </QueryClientProvider>
      </ThemeProvider>
    </CacheProvider>
  );
}
