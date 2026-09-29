export type NotificationPreview = {
  channel: 'SMS' | 'WhatsApp' | 'Email';
  recipient: string;
  message: string;
};

export function previewAppointmentReminder(patientName: string, startsAt: string): NotificationPreview {
  return {
    channel: 'WhatsApp',
    recipient: patientName,
    message: `Reminder: your MediConnect appointment is scheduled for ${startsAt}.`
  };
}
