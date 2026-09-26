package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.LabResultRequest;
import com.smarthospital.prateek.dto.LabTestRequest;
import com.smarthospital.prateek.dto.LabTestResponse;
import com.smarthospital.prateek.entity.*;
import com.smarthospital.prateek.repository.ConsultationRepository;
import com.smarthospital.prateek.repository.LabTestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LabTestService {

    private final LabTestRepository labTestRepository;
    private final ConsultationRepository consultationRepository;

    public LabTestService(
            LabTestRepository labTestRepository,
            ConsultationRepository consultationRepository) {

        this.labTestRepository = labTestRepository;
        this.consultationRepository = consultationRepository;
    }

    public LabTestResponse orderTest(
            LabTestRequest request) {

        Consultation consultation =
                consultationRepository.findById(
                                request.getConsultationId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Consultation not found with id: "
                                                + request.getConsultationId()));

        LabTest labTest = new LabTest();

        labTest.setConsultation(consultation);
        labTest.setPatient(consultation.getPatient());
        labTest.setDoctor(consultation.getDoctor());
        labTest.setTestName(request.getTestName());
        labTest.setInstructions(request.getInstructions());
        labTest.setStatus(LabTestStatus.PENDING);
        labTest.setOrderedAt(LocalDateTime.now());

        return toResponse(
                labTestRepository.save(labTest)
        );
    }

    public List<LabTestResponse> getAllTests() {

        return labTestRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LabTestResponse getTestById(Long id) {

        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Lab test not found with id: " + id));

        return toResponse(labTest);
    }

    public LabTestResponse startTest(Long id) {

        LabTest labTest = getEntityById(id);

        if (labTest.getStatus() != LabTestStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending tests can be started");
        }

        labTest.setStatus(LabTestStatus.IN_PROGRESS);

        return toResponse(
                labTestRepository.save(labTest)
        );
    }

    public LabTestResponse completeTest(
            Long id,
            LabResultRequest request) {

        LabTest labTest = getEntityById(id);

        if (labTest.getStatus() != LabTestStatus.IN_PROGRESS) {
            throw new RuntimeException(
                    "Test must be in progress before completing");
        }

        labTest.setResult(request.getResult());
        labTest.setReportNotes(request.getReportNotes());
        labTest.setStatus(LabTestStatus.REPORT_READY);
        labTest.setCompletedAt(LocalDateTime.now());

        return toResponse(
                labTestRepository.save(labTest)
        );
    }

    public List<LabTestResponse> getPatientTests(
            Long patientId) {

        return labTestRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LabTestResponse> getDoctorTests(
            Long doctorId) {

        return labTestRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LabTestResponse> getPendingTests() {

        return labTestRepository
                .findByStatus(LabTestStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LabTestResponse> getReadyReports() {

        return labTestRepository
                .findByStatus(LabTestStatus.REPORT_READY)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LabTest getEntityById(Long id) {

        return labTestRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Lab test not found with id: " + id));
    }

    private LabTestResponse toResponse(LabTest labTest) {

        return new LabTestResponse(
                labTest.getId(),

                labTest.getConsultation().getId(),

                labTest.getPatient().getId(),
                labTest.getPatient().getName(),

                labTest.getDoctor().getId(),
                labTest.getDoctor().getName(),

                labTest.getTestName(),
                labTest.getInstructions(),

                labTest.getStatus(),

                labTest.getResult(),
                labTest.getReportNotes(),

                labTest.getOrderedAt(),
                labTest.getCompletedAt()
        );
    }
}