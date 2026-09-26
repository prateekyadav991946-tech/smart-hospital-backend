package com.smarthospital.prateek.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryResponse {

    private PatientResponse patient;

    private List<AppointmentResponse> appointments;

    private List<QueueHistoryResponse> queueHistory;

    private List<ConsultationResponse> consultations;

    private List<PrescriptionResponse> prescriptions;

    private List<LabTestResponse> labReports;
}