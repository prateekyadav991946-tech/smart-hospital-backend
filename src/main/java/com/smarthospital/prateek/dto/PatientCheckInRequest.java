package com.smarthospital.prateek.dto;

import com.smarthospital.prateek.entity.QueuePriority;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientCheckInRequest {

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    private QueuePriority priority = QueuePriority.NORMAL;
}
