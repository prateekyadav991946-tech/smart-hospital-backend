package com.smarthospital.prateek.dto;

import com.smarthospital.prateek.entity.LabTestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabTestResponse {

    private Long id;

    private Long consultationId;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private String testName;
    private String instructions;

    private LabTestStatus status;

    private String result;
    private String reportNotes;

    private LocalDateTime orderedAt;
    private LocalDateTime completedAt;
}