package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.PrescriptionRequest;
import com.smarthospital.prateek.dto.PrescriptionResponse;
import com.smarthospital.prateek.entity.Consultation;
import com.smarthospital.prateek.entity.Prescription;
import com.smarthospital.prateek.repository.ConsultationRepository;
import com.smarthospital.prateek.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            ConsultationRepository consultationRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.consultationRepository = consultationRepository;
    }

    public PrescriptionResponse addPrescription(
            PrescriptionRequest request) {

        Consultation consultation =
                consultationRepository.findById(
                                request.getConsultationId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Consultation not found with id: "
                                                + request.getConsultationId()));

        Prescription prescription = new Prescription();

        prescription.setConsultation(consultation);
        prescription.setMedicineName(request.getMedicineName());
        prescription.setDosage(request.getDosage());
        prescription.setFrequency(request.getFrequency());
        prescription.setDuration(request.getDuration());
        prescription.setInstructions(request.getInstructions());

        return toResponse(
                prescriptionRepository.save(prescription)
        );
    }

    public List<PrescriptionResponse> getPrescriptions(
            Long consultationId) {

        consultationRepository.findById(consultationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Consultation not found with id: "
                                        + consultationId));

        return prescriptionRepository
                .findByConsultationId(consultationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deletePrescription(Long id) {

        if (!prescriptionRepository.existsById(id)) {
            throw new RuntimeException(
                    "Prescription not found with id: " + id);
        }

        prescriptionRepository.deleteById(id);
    }

    private PrescriptionResponse toResponse(
            Prescription prescription) {

        return new PrescriptionResponse(
                prescription.getId(),
                prescription.getConsultation().getId(),
                prescription.getMedicineName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getDuration(),
                prescription.getInstructions()
        );
    }
}