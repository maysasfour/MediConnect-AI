export type Patient = {
  id: string;
  name: string;
  age: number;
  gender: string;
  phone: string;
  riskLevel: 'Low' | 'Medium' | 'High' | string;
  conditions: string[];
  allergies: string[];
};
