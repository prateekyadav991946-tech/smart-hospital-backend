package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.AuthResponse;
import com.smarthospital.prateek.dto.LoginRequest;
import com.smarthospital.prateek.dto.PatientRegisterRequest;
import com.smarthospital.prateek.entity.Patient;
import com.smarthospital.prateek.entity.Role;
import com.smarthospital.prateek.entity.User;
import com.smarthospital.prateek.repository.PatientRepository;
import com.smarthospital.prateek.repository.UserRepository;
import com.smarthospital.prateek.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registerPatient(
            PatientRegisterRequest request) {

        if (userRepository.existsByUsername(request.getEmail())) {
            throw new RuntimeException(
                    "An account already exists with this email");
        }

        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "A patient already exists with this email");
        }

        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException(
                    "A patient already exists with this phone number");
        }

        Patient patient = new Patient();

        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setGender(request.getGender());
        patient.setAge(request.getAge());
        patient.setBloodGroup(request.getBloodGroup());

        Patient savedPatient = patientRepository.save(patient);

        User user = new User();

        user.setUsername(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.PATIENT);
        user.setEnabled(true);
        user.setPatient(savedPatient);

        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                savedPatient.getId());
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long patientId =
                user.getPatient() != null
                        ? user.getPatient().getId()
                        : null;

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name());

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                patientId);
    }
}