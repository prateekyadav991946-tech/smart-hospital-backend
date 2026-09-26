package com.smarthospital.prateek.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequest {

    private Long appointmentId;

    @NotBlank(message = "Symptoms are required")
    private String symptoms;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String notes;
}