import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';

const schema = z.object({
  email: z.string().email(),
  password: z.string().min(1),
});
type LoginForm = z.infer<typeof schema>;

export function LoginPage() {
  const { t } = useTranslation();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitted },
  } = useForm<LoginForm>();

  const errorList = Object.entries(errors);

  return (
    <Container maxWidth="sm" sx={{ py: 6 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {t('login.title')}
      </Typography>

      {isSubmitted && errorList.length > 0 && (
        <Alert severity="error" role="alert" sx={{ mb: 2 }} data-testid="error-summary">
          <Typography component="h2" variant="subtitle1">
            {t('login.errorSummaryTitle')}
          </Typography>
          <ul>
            {errorList.map(([field]) => (
              <li key={field}>
                <Link href={`#${field}`}>
                  {field === 'email' ? t('login.invalidEmail') : t('login.passwordRequired')}
                </Link>
              </li>
            ))}
          </ul>
        </Alert>
      )}

      <Box
        component="form"
        noValidate
        onSubmit={handleSubmit(() => {
          /* wired to POST /api/v1/auth/login */
        })}
      >
        <Stack spacing={2}>
          <TextField
            id="email"
            type="email"
            label={t('login.email')}
            required
            error={Boolean(errors.email)}
            helperText={errors.email ? t('login.invalidEmail') : ' '}
            {...register('email', { required: true, pattern: /.+@.+\..+/ })}
          />
          <TextField
            id="password"
            type="password"
            label={t('login.password')}
            required
            error={Boolean(errors.password)}
            helperText={errors.password ? t('login.passwordRequired') : ' '}
            {...register('password', { required: true })}
          />
          <Button type="submit" variant="contained">
            {t('login.submit')}
          </Button>
        </Stack>
      </Box>
    </Container>
  );
}
