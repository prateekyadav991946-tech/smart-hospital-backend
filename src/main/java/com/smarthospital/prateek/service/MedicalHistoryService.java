package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.*;
import com.smarthospital.prateek.entity.Patient;
import com.smarthospital.prateek.entity.User;
import com.smarthospital.prateek.repository.*;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalHistoryService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabTestRepository labTestRepository;
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;

    public MedicalHistoryService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            ConsultationRepository consultationRepository,
            PrescriptionRepository prescriptionRepository,
            LabTestRepository labTestRepository,
            QueueRepository queueRepository,
            UserRepository userRepository) {

        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.consultationRepository = consultationRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.labTestRepository = labTestRepository;
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
    }

    public MedicalHistoryResponse getMedicalHistory(Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found with id: " + patientId));

        PatientResponse patientResponse = new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getGender(),
                patient.getAge(),
                patient.getBloodGroup()
        );

        // ---------------- APPOINTMENTS ----------------

        List<AppointmentResponse> appointments =
                appointmentRepository.findAll()
                        .stream()
                        .filter(appointment ->
                                appointment.getPatient()
                                        .getId()
                                        .equals(patientId))
                        .map(appointment ->
                                new AppointmentResponse(
                                        appointment.getId(),

                                        appointment.getPatient().getId(),
                                        appointment.getPatient().getName(),

                                        appointment.getDoctor().getId(),
                                        appointment.getDoctor().getName(),

                                        appointment.getAppointmentDateTime(),
                                        appointment.getReason(),
                                        appointment.getStatus()
                                ))
                        .toList();

        // ---------------- QUEUE HISTORY ----------------

        List<QueueHistoryResponse> queueHistory =
                queueRepository
                        .findByPatientIdOrderByCheckedInAtDesc(patientId)
                        .stream()
                        .map(entry ->
                                new QueueHistoryResponse(
                                        entry.getId(),

                                        entry.getDoctor().getId(),
                                        entry.getDoctor().getName(),

                                        entry.getAppointment() != null
                                                ? entry.getAppointment().getId()
                                                : null,

                                        entry.getPriority(),
                                        entry.getStatus(),

                                        entry.getCheckedInAt(),
                                        entry.getCalledAt(),
                                        entry.getCompletedAt()
                                ))
                        .toList();

        // ---------------- CONSULTATIONS ----------------

        List<ConsultationResponse> consultations =
                consultationRepository.findAll()
                        .stream()
                        .filter(consultation ->
                                consultation.getPatient()
                                        .getId()
                                        .equals(patientId))
                        .map(consultation ->
                                new ConsultationResponse(
                                        consultation.getId(),

                                        consultation.getAppointment()
                                                .getId(),

                                        consultation.getPatient().getId(),
                                        consultation.getPatient().getName(),

                                        consultation.getDoctor().getId(),
                                        consultation.getDoctor().getName(),

                                        consultation.getSymptoms(),
                                        consultation.getDiagnosis(),
                                        consultation.getNotes(),
                                        consultation.getConsultationDate()
                                ))
                        .toList();

        // ---------------- PRESCRIPTIONS ----------------

        List<PrescriptionResponse> prescriptions =
                prescriptionRepository.findAll()
                        .stream()
                        .filter(prescription ->
                                prescription.getConsultation()
                                        .getPatient()
                                        .getId()
                                        .equals(patientId))
                        .map(prescription ->
                                new PrescriptionResponse(
                                        prescription.getId(),

                                        prescription.getConsultation()
                                                .getId(),

                                        prescription.getMedicineName(),
                                        prescription.getDosage(),
                                        prescription.getFrequency(),
                                        prescription.getDuration(),
                                        prescription.getInstructions()
                                ))
                        .toList();

        // ---------------- LAB REPORTS ----------------

        List<LabTestResponse> labReports =
                labTestRepository.findByPatientId(patientId)
                        .stream()
                        .map(test ->
                                new LabTestResponse(
                                        test.getId(),

                                        test.getConsultation().getId(),

                                        test.getPatient().getId(),
                                        test.getPatient().getName(),

                                        test.getDoctor().getId(),
                                        test.getDoctor().getName(),

                                        test.getTestName(),
                                        test.getInstructions(),

                                        test.getStatus(),

                                        test.getResult(),
                                        test.getReportNotes(),

                                        test.getOrderedAt(),
                                        test.getCompletedAt()
                                ))
                        .toList();

        // ---------------- FINAL RESPONSE ----------------

        return new MedicalHistoryResponse(
                patientResponse,
                appointments,
                queueHistory,
                consultations,
                prescriptions,
                labReports
        );
    }

    // =========================================================
    // PATIENT: GET OWN MEDICAL HISTORY
    // =========================================================

    public MedicalHistoryResponse getMyMedicalHistory(
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getPatient() == null) {
            throw new RuntimeException(
                    "No patient profile linked to this account"
            );
        }

        return getMedicalHistory(
                user.getPatient().getId()
        );
    }
}