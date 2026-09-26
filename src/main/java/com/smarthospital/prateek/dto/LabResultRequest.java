package com.smarthospital.prateek.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabResultRequest {

    @NotBlank(message = "Result is required")
    private String result;

    private String reportNotes;
}