import { createTheme, type Theme } from '@mui/material/styles';

/**
 * Builds an MUI theme for the given direction. Colours are chosen to meet
 * WCAG 2.2 AA contrast; the minimum interactive target size is enforced so
 * touch targets are comfortably large.
 */
export function buildTheme(direction: 'ltr' | 'rtl'): Theme {
  return createTheme({
    direction,
    palette: {
      primary: { main: '#00695c' }, // teal 800 — AA on white
      error: { main: '#b00020' },
      background: { default: '#f7f9f9' },
    },
    typography: {
      fontFamily:
        direction === 'rtl'
          ? '"Noto Kufi Arabic", "Segoe UI", Tahoma, sans-serif'
          : '"Inter", "Segoe UI", Roboto, sans-serif',
    },
    components: {
      MuiButton: {
        styleOverrides: {
          // 44px minimum target height (WCAG 2.2 target size).
          root: { minHeight: 44, textTransform: 'none' },
        },
      },
      MuiCssBaseline: {
        styleOverrides: {
          ':focus-visible': { outline: '3px solid #005b4f', outlineOffset: 2 },
        },
      },
    },
  });
}
