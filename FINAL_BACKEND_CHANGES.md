# Final minimal backend changes for frontend-v5

This patch is based on the uploaded backend as-is.

Already present and intentionally NOT changed:
- Patient self-registration: `/api/auth/patient-register`
- Patient self-booking: `POST /api/appointments/my`
- `User.patient` link
- JWT authentication and role-based SecurityConfig
- Existing Patient, Doctor, Appointment, Consultation, Prescription, Lab and Medical History modules

Added/changed only for the current frontend workflow:
1. `PatientCheckInRequest.java`
2. `PatientQueueResponse.java`
3. `QueueRepository.java` — `findByAppointmentId`
4. `QueueService.java` — patient self check-in + patient queue status
5. `QueueController.java` — `/queue/my` and `/queue/my/check-in`
6. `SecurityConfig.java` — allow PATIENT access to those two queue endpoints

Frontend-v5 API expectations now supported:
- GET `/api/patients/me/medical-history`
- GET `/api/queue/my`
- POST `/api/queue/my/check-in`
- POST `/api/appointments/my`
- GET `/api/doctors`
- existing staff queue endpoints
