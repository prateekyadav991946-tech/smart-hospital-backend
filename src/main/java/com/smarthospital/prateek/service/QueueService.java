package com.smarthospital.prateek.service;

import com.smarthospital.prateek.dto.PatientQueueResponse;
import com.smarthospital.prateek.dto.QueueHistoryResponse;
import com.smarthospital.prateek.dto.QueueRequest;
import com.smarthospital.prateek.entity.*;
import com.smarthospital.prateek.repository.AppointmentRepository;
import com.smarthospital.prateek.repository.DoctorRepository;
import com.smarthospital.prateek.repository.PatientRepository;
import com.smarthospital.prateek.repository.QueueRepository;
import com.smarthospital.prateek.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class QueueService {

    private final QueueRepository queueRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public QueueService(
            QueueRepository queueRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            UserRepository userRepository) {

        this.queueRepository = queueRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    // Existing staff/reception check-in
    public QueueEntry checkIn(QueueRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found with id: " + request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Doctor not found with id: " + request.getDoctorId()));

        Appointment appointment = null;

        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Appointment not found with id: " + request.getAppointmentId()));

            if (!appointment.getPatient().getId().equals(patient.getId())) {
                throw new RuntimeException(
                        "Appointment does not belong to this patient");
            }
        }

        QueueEntry entry = new QueueEntry();
        entry.setPatient(patient);
        entry.setDoctor(doctor);
        entry.setAppointment(appointment);
        entry.setPriority(request.getPriority());
        entry.setStatus(QueueStatus.WAITING);
        entry.setCheckedInAt(LocalDateTime.now());

        return queueRepository.save(entry);
    }

    // Patient self check-in from patient portal
    public PatientQueueResponse checkInMyAppointment(
            String username,
            Long appointmentId,
            QueuePriority priority) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getPatient() == null) {
            throw new RuntimeException(
                    "No patient profile linked to this account");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Appointment not found with id: " + appointmentId));

        if (!appointment.getPatient().getId()
                .equals(user.getPatient().getId())) {
            throw new RuntimeException(
                    "You can only check in for your own appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new RuntimeException(
                    "Only scheduled appointments can be checked in");
        }

        QueueEntry existing = queueRepository
                .findByAppointmentId(appointmentId)
                .orElse(null);

        if (existing != null) {
            return toPatientQueueResponse(existing);
        }

        QueueEntry entry = new QueueEntry();
        entry.setPatient(user.getPatient());
        entry.setDoctor(appointment.getDoctor());
        entry.setAppointment(appointment);
        entry.setPriority(
                priority != null ? priority : QueuePriority.NORMAL);
        entry.setStatus(QueueStatus.WAITING);
        entry.setCheckedInAt(LocalDateTime.now());

        return toPatientQueueResponse(
                queueRepository.save(entry));
    }

    // Patient's latest queue entry
    public PatientQueueResponse getMyQueue(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (user.getPatient() == null) {
            throw new RuntimeException(
                    "No patient profile linked to this account");
        }

        return queueRepository
                .findByPatientIdOrderByCheckedInAtDesc(
                        user.getPatient().getId())
                .stream()
                .findFirst()
                .map(this::toPatientQueueResponse)
                .orElse(null);
    }

    // Doctor's waiting queue
    public List<QueueEntry> getDoctorQueue(Long doctorId) {

        doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Doctor not found with id: " + doctorId));

        List<QueueEntry> queue =
                queueRepository.findByDoctorIdAndStatusOrderByCheckedInAtAsc(
                        doctorId,
                        QueueStatus.WAITING
                );

        return sortBySmartPriority(queue);
    }

    public QueueEntry getNextPatient(Long doctorId) {

        List<QueueEntry> queue = getDoctorQueue(doctorId);

        if (queue.isEmpty()) {
            throw new RuntimeException(
                    "No patients waiting in queue");
        }

        return queue.get(0);
    }

    public QueueEntry callPatient(Long id) {

        QueueEntry entry = queueRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Queue entry not found with id: " + id));

        if (entry.getStatus() != QueueStatus.WAITING) {
            throw new RuntimeException(
                    "Only waiting patients can be called");
        }

        entry.setStatus(QueueStatus.CALLED);
        entry.setCalledAt(LocalDateTime.now());

        return queueRepository.save(entry);
    }

    public QueueEntry completePatient(Long id) {

        QueueEntry entry = queueRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Queue entry not found with id: " + id));

        if (entry.getStatus() != QueueStatus.CALLED) {
            throw new RuntimeException(
                    "Only called patients can be completed");
        }

        entry.setStatus(QueueStatus.COMPLETED);
        entry.setCompletedAt(LocalDateTime.now());

        return queueRepository.save(entry);
    }

    private List<QueueEntry> sortBySmartPriority(
            List<QueueEntry> queue) {

        return queue.stream()
                .sorted(
                        Comparator
                                .comparingInt(
                                        (QueueEntry entry) ->
                                                getPriorityScore(entry))
                                .reversed()
                                .thenComparing(
                                        QueueEntry::getCheckedInAt)
                )
                .toList();
    }

    private int getPriorityScore(QueueEntry entry) {

        int priorityScore;

        switch (entry.getPriority()) {
            case EMERGENCY:
                priorityScore = 100;
                break;
            case HIGH:
                priorityScore = 70;
                break;
            case NORMAL:
                priorityScore = 40;
                break;
            case LOW:
                priorityScore = 20;
                break;
            default:
                priorityScore = 0;
        }

        long waitingMinutes = Duration
                .between(entry.getCheckedInAt(), LocalDateTime.now())
                .toMinutes();

        int waitingBonus = (int) (waitingMinutes / 10);

        return priorityScore + waitingBonus;
    }

    private PatientQueueResponse toPatientQueueResponse(
            QueueEntry entry) {

        int patientsAhead = 0;

        if (entry.getStatus() == QueueStatus.WAITING) {
            List<QueueEntry> waiting = getDoctorQueue(
                    entry.getDoctor().getId());

            for (QueueEntry item : waiting) {
                if (item.getId().equals(entry.getId())) {
                    break;
                }
                patientsAhead++;
            }
        }

        int estimatedWaitMinutes = patientsAhead * 10;

        return new PatientQueueResponse(
                entry.getId(),
                entry.getDoctor().getId(),
                entry.getDoctor().getName(),
                entry.getAppointment() != null
                        ? entry.getAppointment().getId()
                        : null,
                entry.getPriority(),
                entry.getStatus(),
                entry.getCheckedInAt(),
                entry.getCalledAt(),
                entry.getCompletedAt(),
                patientsAhead,
                estimatedWaitMinutes
        );
    }

    public List<QueueHistoryResponse> getPatientQueueHistory(
            Long patientId) {

        patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Patient not found with id: " + patientId));

        return queueRepository
                .findByPatientIdOrderByCheckedInAtDesc(patientId)
                .stream()
                .map(entry -> new QueueHistoryResponse(
                        entry.getId(),
                        entry.getDoctor().getId(),
                        entry.getDoctor().getName(),
                        entry.getAppointment() != null
                                ? entry.getAppointment().getId()
                                : null,
                        entry.getPriority(),
                        entry.getStatus(),
                        entry.getCheckedInAt(),
                        entry.getCalledAt(),
                        entry.getCompletedAt()
                ))
                .toList();
    }
}
