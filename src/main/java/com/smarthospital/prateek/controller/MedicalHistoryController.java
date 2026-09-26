package com.smarthospital.prateek.controller;

import com.smarthospital.prateek.dto.MedicalHistoryResponse;
import com.smarthospital.prateek.service.MedicalHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    public MedicalHistoryController(
            MedicalHistoryService medicalHistoryService) {

        this.medicalHistoryService = medicalHistoryService;
    }

    @GetMapping("/{patientId}/medical-history")
    public ResponseEntity<MedicalHistoryResponse>
    getMedicalHistory(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                medicalHistoryService
                        .getMedicalHistory(patientId)
        );
    }
    @GetMapping("/me/medical-history")
    public ResponseEntity<MedicalHistoryResponse> getMyMedicalHistory(
            Authentication authentication) {

        return ResponseEntity.ok(
                medicalHistoryService.getMyMedicalHistory(
                        authentication.getName()
                )
        );
    }
}