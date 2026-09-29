import type { ReactNode } from 'react';

export function MetricCard({ label, value, detail, icon, tone = 'teal' }: { label: string; value: string | number; detail: string; icon: ReactNode; tone?: string }) {
  return (
    <article className="metric-card">
      <div className={`metric-icon ${tone}`}>{icon}</div>
      <div><span>{label}</span><strong>{value}</strong><small>{detail}</small></div>
    </article>
  );
}
