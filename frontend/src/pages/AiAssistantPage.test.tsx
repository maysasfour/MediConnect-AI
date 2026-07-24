import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { AiAssistantPage } from './AiAssistantPage';

describe('AiAssistantPage', () => {
  it('requires consent before showing the symptom input', () => {
    render(<AiAssistantPage />);
    // Consent gate is shown; the input is not yet available.
    expect(screen.getByRole('heading', { level: 1 })).toBeInTheDocument();
    expect(screen.queryByLabelText(/describe your symptoms/i)).not.toBeInTheDocument();
  });

  it('shows the symptom input only after consent', async () => {
    const user = userEvent.setup();
    render(<AiAssistantPage />);
    await user.click(screen.getByRole('button', { name: /i understand and consent/i }));
    expect(screen.getByLabelText(/describe your symptoms/i)).toBeInTheDocument();
  });

  it('interrupts with an emergency banner on a red-flag message', async () => {
    const user = userEvent.setup();
    render(<AiAssistantPage />);
    await user.click(screen.getByRole('button', { name: /i understand and consent/i }));

    await user.type(
      screen.getByLabelText(/describe your symptoms/i),
      'I have severe chest pain and cannot breathe',
    );
    await user.click(screen.getByRole('button', { name: /send/i }));

    // The emergency banner (assertive alert) replaces the normal flow.
    expect(await screen.findByTestId('emergency-banner')).toBeInTheDocument();
    // The input is hidden during the emergency interrupt.
    expect(screen.queryByLabelText(/describe your symptoms/i)).not.toBeInTheDocument();
  });

  it('does not interrupt a routine message', async () => {
    const user = userEvent.setup();
    render(<AiAssistantPage />);
    await user.click(screen.getByRole('button', { name: /i understand and consent/i }));

    await user.type(
      screen.getByLabelText(/describe your symptoms/i),
      'I have had a mild sore throat for two days',
    );
    await user.click(screen.getByRole('button', { name: /send/i }));

    expect(screen.queryByTestId('emergency-banner')).not.toBeInTheDocument();
  });
});
