package com.smarthospital.prateek.dto;

import com.smarthospital.prateek.entity.QueuePriority;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueueRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private Long appointmentId;

    @NotNull(message = "Priority is required")
    private QueuePriority priority;
}