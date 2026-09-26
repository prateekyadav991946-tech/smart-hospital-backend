package com.smarthospital.prateek.controller;

import com.smarthospital.prateek.dto.AdminUserRequest;
import com.smarthospital.prateek.dto.UserResponse;
import com.smarthospital.prateek.service.AdminUserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody AdminUserRequest request) {

        return ResponseEntity.ok(
                adminUserService.createUser(request)
        );
    }
}