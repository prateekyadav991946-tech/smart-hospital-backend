package com.smarthospital.prateek.dto;

import com.smarthospital.prateek.entity.QueuePriority;
import com.smarthospital.prateek.entity.QueueStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueueHistoryResponse {

    private Long id;

    private Long doctorId;
    private String doctorName;

    private Long appointmentId;

    private QueuePriority priority;
    private QueueStatus status;

    private LocalDateTime checkedInAt;
    private LocalDateTime calledAt;
    private LocalDateTime completedAt;
}