INSERT INTO patients (id, full_name, age, gender, phone, risk_level) VALUES
  ('p-1001', 'Omar Nasser', 34, 'Male', '+962790000101', 'Medium'),
  ('p-1002', 'Maya Saleh', 42, 'Female', '+962790000102', 'High'),
  ('p-1003', 'Yousef Karim', 28, 'Male', '+962790000103', 'Low')
ON CONFLICT (id) DO NOTHING;

INSERT INTO appointments (id, patient_id, doctor_name, starts_at, visit_type, status) VALUES
  ('a-501', 'p-1002', 'Dr. Lina Haddad', now() + interval '2 hours', 'Follow-up', 'Confirmed'),
  ('a-502', 'p-1001', 'Dr. Samer Khoury', now() + interval '1 day', 'Respiratory consult', 'Pending')
ON CONFLICT (id) DO NOTHING;

INSERT INTO medical_records (id, patient_id, record_date, doctor_name, diagnosis, notes) VALUES
  ('r-701', 'p-1002', current_date - 12, 'Dr. Lina Haddad', 'Hypertension review', 'Blood pressure trending down after medication adjustment.'),
  ('r-702', 'p-1001', current_date - 25, 'Dr. Samer Khoury', 'Asthma maintenance', 'No acute distress. Reviewed inhaler technique.')
ON CONFLICT (id) DO NOTHING;

INSERT INTO audit_logs (actor, action, resource) VALUES
  ('system', 'seeded demo data', 'database')
ON CONFLICT DO NOTHING;
