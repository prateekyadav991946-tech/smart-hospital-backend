package com.smarthospital.prateek.controller;

import com.smarthospital.prateek.dto.ConsultationRequest;
import com.smarthospital.prateek.dto.ConsultationResponse;
import com.smarthospital.prateek.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(
            ConsultationService consultationService) {

        this.consultationService = consultationService;
    }

    @PostMapping
    public ResponseEntity<ConsultationResponse> createConsultation(
            @Valid @RequestBody ConsultationRequest request) {

        return ResponseEntity.ok(
                consultationService.createConsultation(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ConsultationResponse>>
    getAllConsultations() {

        return ResponseEntity.ok(
                consultationService.getAllConsultations()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationResponse>
    getConsultationById(@PathVariable Long id) {

        return ResponseEntity.ok(
                consultationService.getConsultationById(id)
        );
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ConsultationResponse>
    getByAppointmentId(
            @PathVariable Long appointmentId) {

        return ResponseEntity.ok(
                consultationService
                        .getByAppointmentId(appointmentId)
        );
    }
}