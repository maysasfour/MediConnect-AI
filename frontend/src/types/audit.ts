export type AuditEvent = {
  id: string;
  actor: string;
  action: string;
  resource: string;
  occurredAt: string;
};
