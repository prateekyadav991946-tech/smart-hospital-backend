package com.smarthospital.prateek.controller;

import com.smarthospital.prateek.dto.AuthResponse;
import com.smarthospital.prateek.dto.LoginRequest;
import com.smarthospital.prateek.dto.PatientRegisterRequest;
import com.smarthospital.prateek.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/patient-register")
    public ResponseEntity<AuthResponse> registerPatient(
            @Valid @RequestBody PatientRegisterRequest request) {

        return ResponseEntity.ok(
                authService.registerPatient(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request));
    }
}