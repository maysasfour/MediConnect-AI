-- MediConnect AI — demo seed data (V2)
--
-- FICTIONAL DATA ONLY. No real patient information is used anywhere in this
-- project. All names, ids and contact details below are invented for demos.
-- Every demo account shares the password:  Demo1234!Pass
-- The stored value is a BCrypt(strength 12) hash of that password.

-- Fixed UUIDs so the seed is deterministic across environments.
-- Clinic
INSERT INTO clinic (id, name_en, name_ar, timezone, default_language)
VALUES ('aaaaaaaa-0000-0000-0000-000000000001',
        'Amman Family Care Center', 'مركز عمّان للرعاية العائلية',
        'Asia/Amman', 'ar');

-- Branch
INSERT INTO branch (id, clinic_id, name_en, name_ar, address_line, phone_e164)
VALUES ('bbbbbbbb-0000-0000-0000-000000000001',
        'aaaaaaaa-0000-0000-0000-000000000001',
        'Abdoun Branch', 'فرع عبدون', 'Abdoun, Amman', '+96265000000');

-- Department
INSERT INTO department (id, clinic_id, branch_id, name_en, name_ar, specialty)
VALUES ('cccccccc-0000-0000-0000-000000000001',
        'aaaaaaaa-0000-0000-0000-000000000001',
        'bbbbbbbb-0000-0000-0000-000000000001',
        'General Medicine', 'الطب العام', 'General Medicine');

-- Demo users (BCrypt hash of Demo1234!Pass).
-- Roles: system admin (no clinic), clinic admin, doctor, receptionist, patient.
INSERT INTO user_account
  (id, clinic_id, email, phone_e164, password_hash, full_name_en, full_name_ar,
   active, email_verified, preferred_language)
VALUES
  ('d0000000-0000-0000-0000-000000000001', NULL,
   'sysadmin@demo.mediconnect.local', '+962790000001',
   '$2a$12$xmWJ6ALr6HPPRmGlnWv.uOLPt2TorUzRyT/UGVVDR.wdm/ikH45gm',
   'System Administrator', 'مدير النظام', true, true, 'en'),
  ('d0000000-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000001',
   'admin@demo.mediconnect.local', '+962790000002',
   '$2a$12$xmWJ6ALr6HPPRmGlnWv.uOLPt2TorUzRyT/UGVVDR.wdm/ikH45gm',
   'Clinic Administrator', 'مدير العيادة', true, true, 'ar'),
  ('d0000000-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000001',
   'doctor@demo.mediconnect.local', '+962790000003',
   '$2a$12$xmWJ6ALr6HPPRmGlnWv.uOLPt2TorUzRyT/UGVVDR.wdm/ikH45gm',
   'Dr. Layla Haddad', 'د. ليلى حداد', true, true, 'ar'),
  ('d0000000-0000-0000-0000-000000000004', 'aaaaaaaa-0000-0000-0000-000000000001',
   'reception@demo.mediconnect.local', '+962790000004',
   '$2a$12$xmWJ6ALr6HPPRmGlnWv.uOLPt2TorUzRyT/UGVVDR.wdm/ikH45gm',
   'Reception Desk', 'مكتب الاستقبال', true, true, 'ar'),
  ('d0000000-0000-0000-0000-000000000005', 'aaaaaaaa-0000-0000-0000-000000000001',
   'patient@demo.mediconnect.local', '+962790000005',
   '$2a$12$xmWJ6ALr6HPPRmGlnWv.uOLPt2TorUzRyT/UGVVDR.wdm/ikH45gm',
   'Omar Test-Patient', 'عمر (مريض تجريبي)', true, true, 'ar');

INSERT INTO user_role (user_id, role) VALUES
  ('d0000000-0000-0000-0000-000000000001', 'SYSTEM_ADMIN'),
  ('d0000000-0000-0000-0000-000000000002', 'CLINIC_ADMIN'),
  ('d0000000-0000-0000-0000-000000000003', 'DOCTOR'),
  ('d0000000-0000-0000-0000-000000000004', 'RECEPTIONIST'),
  ('d0000000-0000-0000-0000-000000000005', 'PATIENT');

-- Fictional demo patients.
INSERT INTO patient
  (id, clinic_id, medical_record_number, full_name_en, full_name_ar,
   national_id, date_of_birth, sex, phone_e164, email, blood_group,
   preferred_language, linked_user_id)
VALUES
  ('ee000000-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001',
   'MRN-0001', 'Omar Test-Patient', 'عمر (مريض تجريبي)',
   '9990000001', '1990-06-15', 'MALE', '+962790000005',
   'patient@demo.mediconnect.local', 'O+', 'ar',
   'd0000000-0000-0000-0000-000000000005'),
  ('ee000000-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000001',
   'MRN-0002', 'Sara Example', 'سارة (مثال)',
   '9990000002', '1985-02-20', 'FEMALE', '+962790000006',
   NULL, 'A-', 'ar', NULL);
