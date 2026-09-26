package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.PatientRequest;
import com.smarthospital.prateek.dto.PatientResponse;
import com.smarthospital.prateek.entity.Patient;
import com.smarthospital.prateek.entity.User;
import com.smarthospital.prateek.repository.PatientRepository;
import com.smarthospital.prateek.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientService(
            PatientRepository patientRepository,
            UserRepository userRepository) {

        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public PatientResponse createPatient(PatientRequest request) {

        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (patientRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        Patient patient = new Patient();

        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setGender(request.getGender());
        patient.setAge(request.getAge());
        patient.setBloodGroup(request.getBloodGroup());

        return toResponse(patientRepository.save(patient));
    }

    public List<PatientResponse> getAllPatients() {

        return patientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PatientResponse getPatientById(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found with id: " + id));

        return toResponse(patient);
    }

    public PatientResponse updatePatient(
            Long id,
            PatientRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found with id: " + id));

        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setGender(request.getGender());
        patient.setAge(request.getAge());
        patient.setBloodGroup(request.getBloodGroup());

        return toResponse(patientRepository.save(patient));
    }

    public void deletePatient(Long id) {

        if (!patientRepository.existsById(id)) {
            throw new RuntimeException(
                    "Patient not found with id: " + id);
        }

        patientRepository.deleteById(id);
    }

    public PatientResponse getMyPatient(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getPatient() == null) {
            throw new RuntimeException(
                    "No patient profile linked to this account");
        }

        return toResponse(user.getPatient());
    }

    private PatientResponse toResponse(Patient patient) {

        return new PatientResponse(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getGender(),
                patient.getAge(),
                patient.getBloodGroup()
        );
    }
}