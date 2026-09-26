package com.smarthospital.prateek.controller;

import com.smarthospital.prateek.dto.PrescriptionRequest;
import com.smarthospital.prateek.dto.PrescriptionResponse;
import com.smarthospital.prateek.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(
            PrescriptionService prescriptionService) {

        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<PrescriptionResponse> addPrescription(
            @Valid @RequestBody PrescriptionRequest request) {

        return ResponseEntity.ok(
                prescriptionService.addPrescription(request)
        );
    }

    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<List<PrescriptionResponse>>
    getPrescriptions(
            @PathVariable Long consultationId) {

        return ResponseEntity.ok(
                prescriptionService
                        .getPrescriptions(consultationId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(
            @PathVariable Long id) {

        prescriptionService.deletePrescription(id);

        return ResponseEntity.noContent().build();
    }
}