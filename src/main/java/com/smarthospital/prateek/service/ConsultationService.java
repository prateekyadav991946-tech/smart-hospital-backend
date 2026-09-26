package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.ConsultationRequest;
import com.smarthospital.prateek.dto.ConsultationResponse;
import com.smarthospital.prateek.entity.*;
import com.smarthospital.prateek.repository.AppointmentRepository;
import com.smarthospital.prateek.repository.ConsultationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;

    public ConsultationService(
            ConsultationRepository consultationRepository,
            AppointmentRepository appointmentRepository) {

        this.consultationRepository = consultationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public ConsultationResponse createConsultation(
            ConsultationRequest request) {

        Appointment appointment = appointmentRepository
                .findById(request.getAppointmentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Appointment not found with id: "
                                        + request.getAppointmentId()));

        if (consultationRepository
                .findByAppointmentId(request.getAppointmentId())
                .isPresent()) {

            throw new RuntimeException(
                    "Consultation already exists for this appointment");
        }

        if (appointment.getStatus()
                == AppointmentStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cannot create consultation for cancelled appointment");
        }

        Consultation consultation = new Consultation();

        consultation.setAppointment(appointment);
        consultation.setPatient(appointment.getPatient());
        consultation.setDoctor(appointment.getDoctor());
        consultation.setSymptoms(request.getSymptoms());
        consultation.setDiagnosis(request.getDiagnosis());
        consultation.setNotes(request.getNotes());
        consultation.setConsultationDate(LocalDateTime.now());

        return toResponse(
                consultationRepository.save(consultation)
        );
    }

    public List<ConsultationResponse> getAllConsultations() {

        return consultationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ConsultationResponse getConsultationById(Long id) {

        Consultation consultation = consultationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Consultation not found with id: " + id));

        return toResponse(consultation);
    }

    public ConsultationResponse getByAppointmentId(
            Long appointmentId) {

        Consultation consultation = consultationRepository
                .findByAppointmentId(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Consultation not found for appointment: "
                                        + appointmentId));

        return toResponse(consultation);
    }

    private ConsultationResponse toResponse(
            Consultation consultation) {

        return new ConsultationResponse(
                consultation.getId(),
                consultation.getAppointment().getId(),

                consultation.getPatient().getId(),
                consultation.getPatient().getName(),

                consultation.getDoctor().getId(),
                consultation.getDoctor().getName(),

                consultation.getSymptoms(),
                consultation.getDiagnosis(),
                consultation.getNotes(),
                consultation.getConsultationDate()
        );
    }
}