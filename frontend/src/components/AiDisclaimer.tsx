import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';

/** The standing AI-safety disclosure shown on the assistant and disclaimer pages. */
export function AiDisclaimer() {
  const { t } = useTranslation();
  const points = ['point1', 'point2', 'point3', 'point4', 'point5', 'point6'];
  return (
    <Paper variant="outlined" sx={{ p: 2 }} component="section" aria-labelledby="ai-disclaimer-title">
      <Typography id="ai-disclaimer-title" variant="h6" component="h2" gutterBottom>
        {t('aiDisclaimer.title')}
      </Typography>
      <List dense>
        {points.map((p) => (
          <ListItem key={p} sx={{ display: 'list-item' }}>
            <ListItemText primary={t(`aiDisclaimer.${p}`)} />
          </ListItem>
        ))}
      </List>
    </Paper>
  );
}
