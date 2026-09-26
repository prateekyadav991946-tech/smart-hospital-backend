package com.smarthospital.prateek.dto;

import com.smarthospital.prateek.entity.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private Long id;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private LocalDateTime appointmentDateTime;
    private String reason;
    private AppointmentStatus status;
}