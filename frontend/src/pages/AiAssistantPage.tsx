import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { AiDisclaimer } from '../components/AiDisclaimer';
import { EmergencyBanner } from '../components/EmergencyBanner';
import { looksLikeEmergency } from '../lib/redFlags';

type Turn = { role: 'user' | 'assistant'; text: string };

export function AiAssistantPage() {
  const { t } = useTranslation();
  const [consented, setConsented] = useState(false);
  const [emergency, setEmergency] = useState(false);
  const [input, setInput] = useState('');
  const [turns, setTurns] = useState<Turn[]>([]);

  function send() {
    const text = input.trim();
    if (!text) return;
    // Client-side interrupt for instant feedback; server re-checks authoritatively.
    if (looksLikeEmergency(text)) {
      setEmergency(true);
      setTurns((prev) => [...prev, { role: 'user', text }]);
      setInput('');
      return;
    }
    setTurns((prev) => [
      ...prev,
      { role: 'user', text },
      { role: 'assistant', text: t('assistant.disclaimerShort') },
    ]);
    setInput('');
  }

  if (!consented) {
    return (
      <Container maxWidth="sm" sx={{ py: 6 }}>
        <Paper variant="outlined" sx={{ p: 3 }} component="section" aria-labelledby="consent-title">
          <Typography id="consent-title" variant="h5" component="h1" gutterBottom>
            {t('assistant.consentTitle')}
          </Typography>
          <Typography paragraph>{t('assistant.consentBody')}</Typography>
          <Stack direction="row" spacing={2}>
            <Button variant="contained" onClick={() => setConsented(true)}>
              {t('assistant.consentAccept')}
            </Button>
            <Button variant="text" href="/">
              {t('assistant.consentDecline')}
            </Button>
          </Stack>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="sm" sx={{ py: 4 }}>
      <Typography variant="h5" component="h1" gutterBottom>
        {t('assistant.title')}
      </Typography>
      <Typography variant="body2" color="text.secondary" paragraph>
        {t('assistant.disclaimerShort')}
      </Typography>

      {emergency && (
        <Box sx={{ mb: 2 }}>
          <EmergencyBanner />
        </Box>
      )}

      <Stack spacing={1} sx={{ mb: 2 }} aria-live="polite">
        {turns.map((turn, i) => (
          <Paper key={i} sx={{ p: 1.5 }} variant="outlined">
            <Typography variant="caption" color="text.secondary">
              {turn.role === 'user' ? '🧑' : '🤖'}
            </Typography>
            <Typography>{turn.text}</Typography>
          </Paper>
        ))}
      </Stack>

      {!emergency && (
        <Box component="form" onSubmit={(e) => { e.preventDefault(); send(); }}>
          <Stack direction="row" spacing={1}>
            <TextField
              fullWidth
              multiline
              label={t('assistant.inputLabel')}
              value={input}
              onChange={(e) => setInput(e.target.value)}
            />
            <Button type="submit" variant="contained">
              {t('assistant.send')}
            </Button>
          </Stack>
        </Box>
      )}

      <Box sx={{ mt: 3 }}>
        <AiDisclaimer />
      </Box>
    </Container>
  );
}
