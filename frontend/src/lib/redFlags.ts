/**
 * Lightweight client-side red-flag check used only for immediate UX feedback
 * (defense in depth). The authoritative red-flag detection is performed on the
 * server before any model call; the UI must never rely on this alone.
 */
const PATTERNS: RegExp[] = [
  /chest pain|can'?t breathe|cannot breathe|slurred speech|severe bleeding|kill myself|unconscious|worst headache/i,
  /ألم في الصدر|الم في الصدر|لا استطيع التنفس|صعوبة في التنفس|نزيف حاد|انتحار|فقدان الوعي/,
];

export function looksLikeEmergency(text: string): boolean {
  return PATTERNS.some((p) => p.test(text));
}
