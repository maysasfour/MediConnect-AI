# FHIR Preparation — Resource Mappings

MediConnect AI does **not** implement a full FHIR server in v1, but the domain
and API are designed to map cleanly to HL7 FHIR R4 so a facade/read API can be
added later without reshaping the model. Below are the intended mappings.

## Patient → `Patient`
| Domain | FHIR |
|--------|------|
| `Patient.id` | `Patient.id` |
| `fullNameEn` / `fullNameAr` | `Patient.name[]` (with `language` extension) |
| `nationalId` / `passportNumber` | `Patient.identifier[]` (system-typed) |
| `dateOfBirth` | `Patient.birthDate` |
| `sex` | `Patient.gender` |
| `phoneE164` / `email` | `Patient.telecom[]` |
| `preferredLanguage` | `Patient.communication[].language` |
| `clinicId` | `Patient.managingOrganization` |

## DoctorProfile / StaffProfile → `Practitioner` (+ `PractitionerRole`)
| Domain | FHIR |
|--------|------|
| provider identity | `Practitioner.identifier`, `.name` |
| specialty, department, clinic | `PractitionerRole.specialty` / `.organization` |

## Appointment → `Appointment`
| Domain | FHIR |
|--------|------|
| `status` | `Appointment.status` (mapped: REQUESTED→booked/pending, CONFIRMED→booked, CHECKED_IN→checked-in, IN_PROGRESS→checked-in, COMPLETED→fulfilled, CANCELLED→cancelled, NO_SHOW→noshow) |
| `startTime` / `endTime` | `Appointment.start` / `.end` |
| doctor / patient | `Appointment.participant[]` (actor references) |

## Encounter → `Encounter`
| Domain | FHIR |
|--------|------|
| `Encounter` | `Encounter.id`, `.status`, `.class` |
| patient / practitioner | `Encounter.subject` / `.participant` |
| period | `Encounter.period` |

## VitalSign → `Observation` (vital-signs category)
| Domain | FHIR |
|--------|------|
| measurement type | `Observation.code` (LOINC) |
| value/unit | `Observation.valueQuantity` |
| encounter | `Observation.encounter` |

## Diagnosis / MedicalCondition → `Condition`
| Domain | FHIR |
|--------|------|
| diagnosis code/label | `Condition.code` (ICD-10/SNOMED) |
| clinical status | `Condition.clinicalStatus` |
| onset | `Condition.onset[x]` |

## Prescription / PrescriptionItem → `MedicationRequest`
| Domain | FHIR |
|--------|------|
| item medication | `MedicationRequest.medication[x]` |
| dose/frequency/route/duration | `MedicationRequest.dosageInstruction[]` |
| status (active/discontinued) | `MedicationRequest.status` |
| prescriber | `MedicationRequest.requester` |

## Document → `DocumentReference`
| Domain | FHIR |
|--------|------|
| upload metadata | `DocumentReference.type`, `.date`, `.author` |
| stored file | `DocumentReference.content.attachment` |

## Notes
- Identifiers use typed systems (national id vs passport vs MRN).
- Terminology binding (LOINC/ICD-10/SNOMED) is a later step; the model keeps
  code + display fields to attach these without migration pain.
- A FHIR facade would live in a dedicated `fhir` module exposing read endpoints,
  reusing existing repositories under the same tenant guard.
