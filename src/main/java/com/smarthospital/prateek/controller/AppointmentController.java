package com.smarthospital.prateek.controller;

import com.smarthospital.prateek.dto.AppointmentRequest;
import com.smarthospital.prateek.dto.AppointmentResponse;
import com.smarthospital.prateek.dto.PatientAppointmentRequest;
import com.smarthospital.prateek.entity.AppointmentStatus;
import com.smarthospital.prateek.service.AppointmentService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService) {

        this.appointmentService = appointmentService;
    }

    // =========================================================
    // PATIENT SELF BOOKING
    // =========================================================

    @PostMapping("/my")
    public ResponseEntity<AppointmentResponse> createMyAppointment(
            Authentication authentication,
            @Valid @RequestBody PatientAppointmentRequest request) {

        return ResponseEntity.ok(
                appointmentService.createMyAppointment(
                        authentication.getName(),
                        request
                )
        );
    }

    // =========================================================
    // STAFF BOOKING
    // =========================================================

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request) {

        return ResponseEntity.ok(
                appointmentService.createAppointment(request)
        );
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>>
    getAllAppointments() {

        return ResponseEntity.ok(
                appointmentService.getAllAppointments()
        );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse>
    getAppointmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentById(id)
        );
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse>
    updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest request) {

        return ResponseEntity.ok(
                appointmentService.updateAppointment(
                        id,
                        request
                )
        );
    }

    // =========================================================
    // STATUS
    // =========================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse>
    updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {

        return ResponseEntity.ok(
                appointmentService.updateStatus(
                        id,
                        status
                )
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable Long id) {

        appointmentService.deleteAppointment(id);

        return ResponseEntity.noContent().build();
    }
}