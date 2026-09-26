package com.smarthospital.prateek.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationResponse {

    private Long id;

    private Long appointmentId;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private String symptoms;
    private String diagnosis;
    private String notes;
    private LocalDateTime consultationDate;
}