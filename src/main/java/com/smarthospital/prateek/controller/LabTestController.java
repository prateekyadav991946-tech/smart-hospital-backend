package com.smarthospital.prateek.controller;

import com.smarthospital.prateek.dto.LabResultRequest;
import com.smarthospital.prateek.dto.LabTestRequest;
import com.smarthospital.prateek.dto.LabTestResponse;
import com.smarthospital.prateek.service.LabTestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lab-tests")
public class LabTestController {

    private final LabTestService labTestService;

    public LabTestController(LabTestService labTestService) {
        this.labTestService = labTestService;
    }

    @PostMapping
    public ResponseEntity<LabTestResponse> orderTest(
            @Valid @RequestBody LabTestRequest request) {

        return ResponseEntity.ok(
                labTestService.orderTest(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<LabTestResponse>> getAllTests() {

        return ResponseEntity.ok(
                labTestService.getAllTests()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabTestResponse> getTestById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                labTestService.getTestById(id)
        );
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<LabTestResponse> startTest(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                labTestService.startTest(id)
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<LabTestResponse> completeTest(
            @PathVariable Long id,
            @Valid @RequestBody LabResultRequest request) {

        return ResponseEntity.ok(
                labTestService.completeTest(id, request)
        );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<LabTestResponse>>
    getPatientTests(@PathVariable Long patientId) {

        return ResponseEntity.ok(
                labTestService.getPatientTests(patientId)
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<LabTestResponse>>
    getDoctorTests(@PathVariable Long doctorId) {

        return ResponseEntity.ok(
                labTestService.getDoctorTests(doctorId)
        );
    }

    @GetMapping("/status/pending")
    public ResponseEntity<List<LabTestResponse>>
    getPendingTests() {

        return ResponseEntity.ok(
                labTestService.getPendingTests()
        );
    }

    @GetMapping("/status/ready")
    public ResponseEntity<List<LabTestResponse>>
    getReadyReports() {

        return ResponseEntity.ok(
                labTestService.getReadyReports()
        );
    }
}