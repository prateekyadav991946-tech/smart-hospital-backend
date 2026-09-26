package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.AdminUserRequest;
import com.smarthospital.prateek.dto.UserResponse;
import com.smarthospital.prateek.entity.Patient;
import com.smarthospital.prateek.entity.Role;
import com.smarthospital.prateek.entity.User;
import com.smarthospital.prateek.repository.PatientRepository;
import com.smarthospital.prateek.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(AdminUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(request.getRole());
        user.setEnabled(true);

        if (request.getRole() == Role.PATIENT) {

            if (request.getPatientId() == null) {
                throw new RuntimeException(
                        "Patient ID is required for PATIENT role"
                );
            }

            Patient patient = patientRepository.findById(
                    request.getPatientId()
            ).orElseThrow(() ->
                    new RuntimeException(
                            "Patient not found with id: "
                                    + request.getPatientId()
                    ));

            user.setPatient(patient);
        }

        return toResponse(userRepository.save(user));
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getPatient() != null
                        ? user.getPatient().getId()
                        : null,
                user.getEnabled()
        );
    }
}