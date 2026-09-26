package com.smarthospital.prateek.controller;

import com.smarthospital.prateek.dto.PatientCheckInRequest;
import com.smarthospital.prateek.dto.PatientQueueResponse;
import com.smarthospital.prateek.dto.QueueRequest;
import com.smarthospital.prateek.entity.QueueEntry;
import com.smarthospital.prateek.service.QueueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    // Patient portal: self check-in
    @PostMapping("/my/check-in")
    public ResponseEntity<PatientQueueResponse> patientCheckIn(
            Authentication authentication,
            @Valid @RequestBody PatientCheckInRequest request) {

        return ResponseEntity.ok(
                queueService.checkInMyAppointment(
                        authentication.getName(),
                        request.getAppointmentId(),
                        request.getPriority()
                )
        );
    }

    // Patient portal: latest queue status
    @GetMapping("/my")
    public ResponseEntity<PatientQueueResponse> getMyQueue(
            Authentication authentication) {

        return ResponseEntity.ok(
                queueService.getMyQueue(
                        authentication.getName()
                )
        );
    }

    // Existing staff/reception check-in
    @PostMapping("/check-in")
    public ResponseEntity<QueueEntry> checkIn(
            @Valid @RequestBody QueueRequest request) {

        return ResponseEntity.ok(
                queueService.checkIn(request)
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<QueueEntry>> getDoctorQueue(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                queueService.getDoctorQueue(doctorId)
        );
    }

    @GetMapping("/doctor/{doctorId}/next")
    public ResponseEntity<QueueEntry> getNextPatient(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                queueService.getNextPatient(doctorId)
        );
    }

    @PutMapping("/{id}/call")
    public ResponseEntity<QueueEntry> callPatient(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                queueService.callPatient(id)
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<QueueEntry> completePatient(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                queueService.completePatient(id)
        );
    }
}
