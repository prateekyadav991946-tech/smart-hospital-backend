package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.DoctorRequest;
import com.smarthospital.prateek.dto.DoctorResponse;
import com.smarthospital.prateek.entity.Doctor;
import com.smarthospital.prateek.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorResponse createDoctor(DoctorRequest request) {

        Doctor doctor = new Doctor();

        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setQualification(request.getQualification());
        doctor.setExperience(request.getExperience());
        doctor.setDepartment(request.getDepartment());

        if (request.getAvailable() != null) {
            doctor.setAvailable(request.getAvailable());
        }

        return toResponse(doctorRepository.save(doctor));
    }

    public List<DoctorResponse> getAllDoctors() {

        return doctorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DoctorResponse getDoctorById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Doctor not found with id: " + id));

        return toResponse(doctor);
    }

    public DoctorResponse updateDoctor(
            Long id,
            DoctorRequest request) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Doctor not found with id: " + id));

        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setPhone(request.getPhone());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setQualification(request.getQualification());
        doctor.setExperience(request.getExperience());
        doctor.setDepartment(request.getDepartment());

        if (request.getAvailable() != null) {
            doctor.setAvailable(request.getAvailable());
        }

        return toResponse(doctorRepository.save(doctor));
    }

    public void deleteDoctor(Long id) {

        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException(
                    "Doctor not found with id: " + id);
        }

        doctorRepository.deleteById(id);
    }

    private DoctorResponse toResponse(Doctor doctor) {

        return new DoctorResponse(
                doctor.getId(),
                doctor.getName(),
                doctor.getEmail(),
                doctor.getPhone(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getExperience(),
                doctor.getDepartment(),
                doctor.getAvailable()
        );
    }
}