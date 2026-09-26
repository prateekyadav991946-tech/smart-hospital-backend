package com.smarthospital.prateek.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {

    private Long id;

    private Long consultationId;

    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
}