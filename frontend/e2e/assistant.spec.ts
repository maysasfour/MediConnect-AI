import { expect, test } from '@playwright/test';

test.describe('AI symptom assistant safety', () => {
  test('requires consent, then interrupts on an emergency red flag', async ({ page }) => {
    await page.goto('/assistant');

    // Consent gate is shown first.
    await expect(page.getByRole('heading', { level: 1 })).toBeVisible();
    await page.getByRole('button', { name: /i understand and consent/i }).click();

    // Symptom input appears after consent.
    const input = page.getByLabel(/describe your symptoms/i);
    await expect(input).toBeVisible();

    // An emergency message interrupts the chat.
    await input.fill('I have severe chest pain and cannot breathe');
    await page.getByRole('button', { name: /send/i }).click();

    await expect(page.getByTestId('emergency-banner')).toBeVisible();
    await expect(page.getByRole('link', { name: /call 911/i })).toBeVisible();
  });
});

test('language toggle switches document direction to RTL', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('button', { name: 'العربية' }).click();
  await expect(page.locator('html')).toHaveAttribute('dir', 'rtl');
});
