import { apiRequest } from '../api/axios';
import { endpoints } from '../api/endpoints';

export type AiSummary = {
  summary: string;
  safetyNotes: string[];
  suggestedNextSteps: string[];
};

export function getAiSummary(message: string, patientId?: string): Promise<AiSummary> {
  return apiRequest<AiSummary>(endpoints.aiSummary, {
    method: 'POST',
    body: JSON.stringify({ message, patientId })
  });
}
