package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.AppointmentRequest;
import com.smarthospital.prateek.dto.AppointmentResponse;
import com.smarthospital.prateek.dto.PatientAppointmentRequest;
import com.smarthospital.prateek.entity.*;
import com.smarthospital.prateek.repository.AppointmentRepository;
import com.smarthospital.prateek.repository.DoctorRepository;
import com.smarthospital.prateek.repository.PatientRepository;
import com.smarthospital.prateek.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            UserRepository userRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // NORMAL STAFF APPOINTMENT CREATION
    // =========================================================

    public AppointmentResponse createAppointment(
            AppointmentRequest request) {

        Patient patient = patientRepository.findById(
                request.getPatientId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Patient not found with id: "
                                + request.getPatientId()
                ));

        Doctor doctor = doctorRepository.findById(
                request.getDoctorId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Doctor not found with id: "
                                + request.getDoctorId()
                ));

        if (!doctor.getAvailable()) {
            throw new RuntimeException(
                    "Doctor is currently unavailable"
            );
        }

        if (appointmentRepository
                .existsByDoctorIdAndAppointmentDateTime(
                        request.getDoctorId(),
                        request.getAppointmentDateTime())) {

            throw new RuntimeException(
                    "This time slot is already booked"
            );
        }

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(
                request.getAppointmentDateTime()
        );
        appointment.setReason(request.getReason());
        appointment.setStatus(
                AppointmentStatus.SCHEDULED
        );

        return toResponse(
                appointmentRepository.save(appointment)
        );
    }

    // =========================================================
    // PATIENT SELF BOOKING
    // =========================================================

    public AppointmentResponse createMyAppointment(
            String username,
            PatientAppointmentRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        if (user.getPatient() == null) {
            throw new RuntimeException(
                    "No patient profile linked to this account"
            );
        }

        Patient patient = user.getPatient();

        Doctor doctor = doctorRepository.findById(
                request.getDoctorId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Doctor not found with id: "
                                + request.getDoctorId()
                ));

        if (!doctor.getAvailable()) {
            throw new RuntimeException(
                    "Doctor is currently unavailable"
            );
        }

        if (appointmentRepository
                .existsByDoctorIdAndAppointmentDateTime(
                        request.getDoctorId(),
                        request.getAppointmentDateTime())) {

            throw new RuntimeException(
                    "This time slot is already booked"
            );
        }

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(
                request.getAppointmentDateTime()
        );
        appointment.setReason(request.getReason());
        appointment.setStatus(
                AppointmentStatus.SCHEDULED
        );

        return toResponse(
                appointmentRepository.save(appointment)
        );
    }

    // =========================================================
    // GET ALL
    // =========================================================

    public List<AppointmentResponse> getAllAppointments() {

        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    public AppointmentResponse getAppointmentById(Long id) {

        Appointment appointment =
                appointmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment not found with id: "
                                                + id
                                ));

        return toResponse(appointment);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public AppointmentResponse updateAppointment(
            Long id,
            AppointmentRequest request) {

        Appointment appointment =
                appointmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment not found with id: "
                                                + id
                                ));

        Patient patient =
                patientRepository.findById(
                        request.getPatientId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found"
                        ));

        Doctor doctor =
                doctorRepository.findById(
                        request.getDoctorId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Doctor not found"
                        ));

        if (!doctor.getAvailable()) {
            throw new RuntimeException(
                    "Doctor is currently unavailable"
            );
        }

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(
                request.getAppointmentDateTime()
        );
        appointment.setReason(request.getReason());

        return toResponse(
                appointmentRepository.save(appointment)
        );
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    public AppointmentResponse updateStatus(
            Long id,
            AppointmentStatus status) {

        Appointment appointment =
                appointmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment not found with id: "
                                                + id
                                ));

        appointment.setStatus(status);

        return toResponse(
                appointmentRepository.save(appointment)
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void deleteAppointment(Long id) {

        if (!appointmentRepository.existsById(id)) {

            throw new RuntimeException(
                    "Appointment not found with id: " + id
            );
        }

        appointmentRepository.deleteById(id);
    }

    // =========================================================
    // RESPONSE MAPPER
    // =========================================================

    private AppointmentResponse toResponse(
            Appointment appointment) {

        return new AppointmentResponse(
                appointment.getId(),

                appointment.getPatient().getId(),
                appointment.getPatient().getName(),

                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),

                appointment.getAppointmentDateTime(),
                appointment.getReason(),
                appointment.getStatus()
        );
    }
}