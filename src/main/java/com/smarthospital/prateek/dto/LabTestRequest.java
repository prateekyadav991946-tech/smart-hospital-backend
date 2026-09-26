package com.smarthospital.prateek.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabTestRequest {

    @NotNull(message = "Consultation ID is required")
    private Long consultationId;

    @NotBlank(message = "Test name is required")
    private String testName;

    private String instructions;
}